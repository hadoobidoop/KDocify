package com.bambi.kdocify
import com.bambi.kdocify.domain.getDefaultCommentName
import com.bambi.kdocify.generators.ClassKDocGenerator
import com.bambi.kdocify.generators.InterfaceKDocGenerator
import com.bambi.kdocify.generators.NamedFunctionKDocGenerator
import com.bambi.kdocify.generators.SecondaryConstructorKDocGenerator
import com.bambi.kdocify.settings.AppSettingsState
import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.text.CharArrayUtil
import org.jetbrains.kotlin.idea.kdoc.KDocElementFactory
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType


class CommentGenerationEnterHandlerDelegateAdapter : EnterHandlerDelegateAdapter() {
    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): EnterHandlerDelegate.Result {
        if (file !is KtFile || !CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER)
            return EnterHandlerDelegate.Result.Continue

        val caretModel = editor.caretModel
        if (!isInKDoc(editor, caretModel.offset))
            return EnterHandlerDelegate.Result.Continue

        val project = file.project
        val documentManager = PsiDocumentManager.getInstance(project)
        documentManager.commitAllDocuments()

        val elementAtCaret = file.findElementAt(caretModel.offset)
        val kdoc = PsiTreeUtil.getParentOfType(elementAtCaret, KDoc::class.java)
            ?: return EnterHandlerDelegate.Result.Continue
        val kdocSection = kdoc.getChildOfType<KDocSection>() ?: return EnterHandlerDelegate.Result.Continue

        if (kdocSection.text.trim() != "*")
            return EnterHandlerDelegate.Result.Continue

        ApplicationManager.getApplication().runWriteAction {
            val kDocElementFactory = KDocElementFactory(project)

            val parent = kdoc.parent
            when {
                parent is KtNamedFunction -> NamedFunctionKDocGenerator(parent)
                parent is KtClass && parent.isInterface() -> InterfaceKDocGenerator(parent)
                parent is KtClass -> ClassKDocGenerator(parent)
                parent is KtSecondaryConstructor -> SecondaryConstructorKDocGenerator(parent)
                else -> null
            }?.getGeneratedComment()?.let {
                kDocElementFactory.createKDocFromText(it.toString())
                    .let { kdoc.replace(it) }
                    .let { CodeStyleManager.getInstance(project).reformat(it) }
            }?.let {
                it.getChildOfType<KDocSection>()?.let {
                    caretModel.moveToOffset(it.textOffset + 6)
                }
            } ?: getDefaultCommentName(
                serviceName = AppSettingsState.status.serviceName,
                title = parent.text,
                withDot = false
            )
        }
        return EnterHandlerDelegate.Result.Continue
    }
    private fun isInKDoc(editor: Editor, offset: Int): Boolean {
        val document = editor.document
        val docChars = document.charsSequence
        var i = CharArrayUtil.lastIndexOf(docChars, "/**", offset)
        if (i >= 0) {
            i = CharArrayUtil.indexOf(docChars, "*/", i)
            return i > offset
        }
        return false
    }
}