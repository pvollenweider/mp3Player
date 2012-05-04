// FIXME: put this with an addResource mechanisme...
println("<script type=\"text/javascript\" src=\"/modules/mp3Player/javascript/jquery.swfobject.min.js\"></script>\n" +
        "<script type=\"text/javascript\">\n" +
        "\$(document).ready(function() {\n" +
            "\$(\"a[href\$='.mp3']\").each(function() {\n" +
             "title = \$(this).attr('title');\n"+
             "if (title != \"Download\"){\n" +

              "audio_file = \$(this).attr('href');\n" +

                "div = \$('<div class=\"audio_flash\" id=\"' + audio_file + '\"></div>');\n" +
                "\$(this).after(div);\n" +
                "//\$(this).remove();\n" +
                "div.flash({\n" +
                    "swf: '/modules/mp3Player/player/player.swf',\n" +
                    "flashvars: {\n" +
                        "soundFile: audio_file,\n" +
                        "playerID: \"'\" + audio_file + \"'\",\n" +
                        "quality: 'high',\n" +
                        "lefticon: '0x${lefticon}',\n" +
                        "righticon: '0x${righticon}',\n" +
                        "leftbg: '0x${leftbg}',\n" +
                        "rightbg: '0x${rightbg}',\n" +
                        "rightbghover: '0x${rightbghover}',\n" +
                        "wmode: 'transparent',\n" +
                        "initialvolume: '85'\n" +
                    "},\n" +
                    "height: ${height}\n" +
                "});\n" +
                "}\n"+
            "});\n" +
        "});\n" +
        "</script>")
