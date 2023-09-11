package com.bambi.kdocify.generators

import com.bambi.kdocify.domain.Kdoc

interface KDocGenerator {
    fun getGeneratedComment(): Kdoc
}




