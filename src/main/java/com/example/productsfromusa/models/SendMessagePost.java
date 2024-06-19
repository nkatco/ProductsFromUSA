package com.example.productsfromusa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Data
@AllArgsConstructor
public class SendMessagePost {
    private String text;
    private InputFile photo;
}
