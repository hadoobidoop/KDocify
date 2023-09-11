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
        if (!file.shouldProcessForKDoc(editor))
            return EnterHandlerDelegate.Result.Continue

        generateKDocComment(file, editor)

        return EnterHandlerDelegate.Result.Continue
    }

    private fun PsiFile.shouldProcessForKDoc(editor: Editor): Boolean {
        return this is KtFile &&
                CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER &&
                editor.isInKDoc()
    }

    private fun Editor.isInKDoc(): Boolean {
        val docChars = document.charsSequence
        val start = CharArrayUtil.lastIndexOf(docChars, "/**", caretModel.offset)
        return if (start >= 0) {
            val end = CharArrayUtil.indexOf(docChars, "*/", start)
            end > caretModel.offset
        } else false
    }

    private fun generateKDocComment(file: PsiFile, editor: Editor) {
        val project = file.project
        val elementAtCaret = file.findElementAt(editor.caretModel.offset)
        val kdoc = PsiTreeUtil.getParentOfType(elementAtCaret, KDoc::class.java) ?: return

        val kdocSection = kdoc.getChildOfType<KDocSection>() ?: return
        if (kdocSection.text.trim() != "*") return

        ApplicationManager.getApplication().runWriteAction {
            val kDocElementFactory = KDocElementFactory(project)
            val parent = kdoc.parent
            val kdocGenerator = when {
                parent is KtNamedFunction -> NamedFunctionKDocGenerator(parent)
                parent is KtClass && parent.isInterface() -> InterfaceKDocGenerator(parent)
                parent is KtClass -> ClassKDocGenerator(parent)
                parent is KtSecondaryConstructor -> SecondaryConstructorKDocGenerator(parent)
                else -> null
            }

            kdocGenerator?.getGeneratedComment()?.let {
                val newKdoc = kDocElementFactory.createKDocFromText(it.toString())
                val formattedKdoc = CodeStyleManager.getInstance(project).reformat(newKdoc)
                kdoc.replace(formattedKdoc)
                editor.caretModel.moveToOffset(
                    formattedKdoc.getChildOfType<KDocSection>()?.textOffset ?: (0 + OFFSET_CONST)
                )
            } ?: getDefaultCommentName(AppSettingsState.status.serviceName, parent.text, false)
        }
    }

    companion object {
        const val OFFSET_CONST = 6
    }
}
