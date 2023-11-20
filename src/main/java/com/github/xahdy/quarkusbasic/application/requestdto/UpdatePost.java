package com.github.xahdy.quarkusbasic.application.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePost {

    String title;
    String content;
}