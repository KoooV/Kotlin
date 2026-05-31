package com.example.nobelprizes.data

import com.example.nobelprizes.domain.Photo

fun PhotoDto.toDomain(): Photo {
    return Photo(
        id = this.id,
        author = this.author,
        width = this.width,
        height = this.height,
        thumbnailUrl = "https://picsum.photos/id/${this.id}/500/500",
        fullUrl = this.downloadUrl
    )
}

