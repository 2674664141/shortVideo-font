package com.xingzhi.shortvideosharingplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merge {

    String fileHash;

    String fileName;

    String fileType;

    String mimeType;

    Integer chunkSize;
}
