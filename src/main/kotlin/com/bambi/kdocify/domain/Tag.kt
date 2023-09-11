package com.bambi.kdocify.domain

sealed class Tag {
    abstract fun asKdocString(): String
    data class Constructor(val name: String) : Tag() {
        override fun asKdocString() = " * @constructor $name"
    }


    data class Property(val name: String?) : Tag() {
        override fun asKdocString() = " * @property $name"
    }

    data class Parameter(val name: String?) : Tag() {
        override fun asKdocString() = " * @param $name"
    }

    data class Receiver(val name: String) : Tag() {
        override fun asKdocString() = " * @receiver $name"
    }

    data class Return(val description: String) : Tag() {
        override fun asKdocString() = " * @return $description"
    }

    data class Since(val version: String) : Tag() {
        override fun asKdocString() = " * @since $version"
    }

    data class Sample(val description: String) : Tag() {
        override fun asKdocString() = " * @sample $description"
    }

    data class See(val link: String) : Tag() {
        override fun asKdocString() = " * @see $link"
    }

    data class Author(val name: String) : Tag() {
        override fun asKdocString() = " * @author $name"
    }
}

class TagListBuilder {
    val tagList = mutableListOf<Tag>()

    /**
     * [안녕] Constructor.
     *
     */
    fun constructor(name: String) {
        tagList.add(Tag.Constructor(name))
    }

    fun parameter(name: String) {
        tagList.add(Tag.Parameter(name))
    }

    fun receiver(name: String) {
        tagList.add(Tag.Receiver(name))
    }

    fun `return`(description: String) {
        tagList.add(Tag.Return(description))
    }

    fun since(version: String) {
        tagList.add(Tag.Since(version))
    }

    fun sample(name: String) {
        tagList.add(Tag.Sample(name))
    }

    fun see(link: String) {
        tagList.add(Tag.See(link))
    }

    fun author(name: String) {
        tagList.add(Tag.Author(name))
    }

    fun addAll(existingTags: List<Tag>) {
        tagList.addAll(existingTags)
    }

    fun build() = tagList
}

