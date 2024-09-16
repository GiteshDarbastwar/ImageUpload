package com.gtasterix.ImageUpload.exception;

public class FileSizeExceededException extends RuntimeException{
    public FileSizeExceededException(String s) {
        super(s);
    }
}
