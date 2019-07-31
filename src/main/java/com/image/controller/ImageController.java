package com.image.controller;

import com.image.model.ImageUploaderDTO;
import com.image.model.Size;
import com.image.parser.ImageResizing;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    @RequestMapping(value = "/resize", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public byte[] resizeImage(@ModelAttribute ImageUploaderDTO uploaderDTO) throws IOException {
        BufferedImage bufferedImage = ImageResizing.createBufferedImageByArray(uploaderDTO.getImage().getBytes());
        byte[] imageByteArray = ImageResizing.resizeImage(bufferedImage, uploaderDTO.getSize());
        return imageByteArray;
    }
}
