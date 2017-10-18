package com.music.android.musixmatch;

/**
 * Created by CTer on 17/5/9.
 */

public class RichSyncResponseBean {

    private MessageBean message;

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public static class MessageBean {

        private HeaderBean header;
        private BodyBean body;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public static class HeaderBean {
            /**
             * status_code : 200
             * available : 1
             * execute_time : 0.023973941802979
             */

            private int status_code;
            private int available;
            private double execute_time;

            public int getStatus_code() {
                return status_code;
            }

            public void setStatus_code(int status_code) {
                this.status_code = status_code;
            }

            public int getAvailable() {
                return available;
            }

            public void setAvailable(int available) {
                this.available = available;
            }

            public double getExecute_time() {
                return execute_time;
            }

            public void setExecute_time(double execute_time) {
                this.execute_time = execute_time;
            }
        }

        public static class BodyBean {
            /**
             * richsync : {"richsync_id":6,"restricted":0,"richsync_body":"[{\"ts\":16.426,\"te\":18.261,\"l\":[{\"c\":\"I\",\"o\":0},{\"c\":\"'\",\"o\":0.013},{\"c\":\"m\",\"o\":0.025},{\"c\":\" \",\"o\":0.088},{\"c\":\"t\",\"o\":0.114},{\"c\":\"r\",\"o\":0.137},{\"c\":\"y\",\"o\":0.163},{\"c\":\"n\",\"o\":0.213},{\"c\":\"a\",\"o\":0.276},{\"c\":\" \",\"o\":0.299},{\"c\":\"p\",\"o\":0.311},{\"c\":\"u\",\"o\":0.348},{\"c\":\"t\",\"o\":0.386},{\"c\":\" \",\"o\":0.399},{\"c\":\"y\",\"o\":0.399},{\"c\":\"o\",\"o\":0.424},{\"c\":\"u\",\"o\":0.45},{\"c\":\" \",\"o\":0.489},{\"c\":\"i\",\"o\":0.501},{\"c\":\"n\",\"o\":0.512},{\"c\":\" \",\"o\":0.549},{\"c\":\"t\",\"o\":0.562},{\"c\":\"h\",\"o\":0.587},{\"c\":\"e\",\"o\":0.626},{\"c\":\" \",\"o\":0.688},{\"c\":\"w\",\"o\":0.7},{\"c\":\"o\",\"o\":0.813},{\"c\":\"r\",\"o\":0.888},{\"c\":\"s\",\"o\":0.911},{\"c\":\"t\",\"o\":0.963},{\"c\":\" \",\"o\":1.026},{\"c\":\"m\",\"o\":1.076},{\"c\":\"o\",\"o\":1.212},{\"c\":\"o\",\"o\":1.288},{\"c\":\"d\",\"o\":1.386},{\"c\":\",\",\"o\":1.489},{\"c\":\" \",\"o\":1.512},{\"c\":\"a\",\"o\":1.563},{\"c\":\"h\",\"o\":1.65}],\"x\":\"I'm tryna put you in the worst mood, ah\"},{\"ts\":18.934,\"te\":21.218,\"l\":[{\"c\":\"P\",\"o\":0},{\"c\":\"1\",\"o\":0.013},{\"c\":\" \",\"o\":0.298},{\"c\":\"c\",\"o\":0.4},{\"c\":\"l\",\"o\":0.536},{\"c\":\"e\",\"o\":0.575},{\"c\":\"a\",\"o\":0.612},{\"c\":\"n\",\"o\":0.65},{\"c\":\"e\",\"o\":0.687},{\"c\":\"r\",\"o\":0.786},{\"c\":\" \",\"o\":0.85},{\"c\":\"t\",\"o\":0.875},{\"c\":\"h\",\"o\":0.911},{\"c\":\"a\",\"o\":0.991},{\"c\":\"n\",\"o\":1.049},{\"c\":\" \",\"o\":1.112},{\"c\":\"y\",\"o\":1.137},{\"c\":\"o\",\"o\":1.188},{\"c\":\"u\",\"o\":1.249},{\"c\":\"r\",\"o\":1.287},{\"c\":\" \",\"o\":1.325},{\"c\":\"c\",\"o\":1.338},{\"c\":\"h\",\"o\":1.4},{\"c\":\"u\",\"o\":1.474},{\"c\":\"r\",\"o\":1.536},{\"c\":\"c\",\"o\":1.575},{\"c\":\"h\",\"o\":1.637},{\"c\":\" \",\"o\":1.702},{\"c\":\"s\",\"o\":1.725},{\"c\":\"h\",\"o\":1.8},{\"c\":\"o\",\"o\":1.875},{\"c\":\"e\",\"o\":1.914},{\"c\":\"s\",\"o\":1.95},{\"c\":\",\",\"o\":2},{\"c\":\" \",\"o\":2.025},{\"c\":\"a\",\"o\":2.038},{\"c\":\"h\",\"o\":2.062}],\"x\":\"P1 cleaner than your church shoes, ah\"},{\"ts\":21.441,\"te\":23.35,\"l\":[{\"c\":\"M\",\"o\":0},{\"c\":\"i\",\"o\":0.011},{\"c\":\"l\",\"o\":0.05},{\"c\":\"l\",\"o\":0.11},{\"c\":\"i\",\"o\":0.198},{\"c\":\" \",\"o\":0.222},{\"c\":\"p\",\"o\":0.248},{\"c\":\"o\",\"o\":0.286},{\"c\":\"i\",\"o\":0.324},{\"c\":\"n\",\"o\":0.349},{\"c\":\"t\",\"o\":0.374},{\"c\":\" \",\"o\":0.399},{\"c\":\"t\",\"o\":0.412},{\"c\":\"w\",\"o\":0.436},{\"c\":\"o\",\"o\":0.474},{\"c\":\" \",\"o\":0.513},{\"c\":\"j\",\"o\":0.525},{\"c\":\"u\",\"o\":0.549},{\"c\":\"s\",\"o\":0.601},{\"c\":\"t\",\"o\":0.662},{\"c\":\" \",\"o\":0.762},{\"c\":\"t\",\"o\":0.811},{\"c\":\"o\",\"o\":0.864},{\"c\":\" \",\"o\":0.937},{\"c\":\"h\",\"o\":0.961},{\"c\":\"u\",\"o\":1.036},{\"c\":\"r\",\"o\":1.1},{\"c\":\"t\",\"o\":1.15},{\"c\":\" \",\"o\":1.187},{\"c\":\"y\",\"o\":1.212},{\"c\":\"o\",\"o\":1.275},{\"c\":\"u\",\"o\":1.376},{\"c\":\",\",\"o\":1.501},{\"c\":\" \",\"o\":1.565},{\"c\":\"a\",\"o\":1.587},{\"c\":\"h\",\"o\":1.675}],\"x\":\"Milli point two just to hurt you, ah\"},{\"ts\":23.949,\"te\":26.224,\"l\":[{\"c\":\"A\",\"o\":0},{\"c\":\"l\",\"o\":0.013},{\"c\":\"l\",\"o\":0.075},{\"c\":\" \",\"o\":0.103},{\"c\":\"r\",\"o\":0.15},{\"c\":\"e\",\"o\":0.249},{\"c\":\"d\",\"o\":0.402},{\"c\":\" \",\"o\":0.477},{\"c\":\"L\",\"o\":0.512},{\"c\":\"a\",\"o\":0.598},{\"c\":\"m\",\"o\":0.661},{\"c\":\"b\",\"o\":0.748},{\"c\":\"'\",\"o\":0.853},{\"c\":\" \",\"o\":0.863},{\"c\":\"j\",\"o\":0.887},{\"c\":\"u\",\"o\":0.936},{\"c\":\"s\",\"o\":1.05},{\"c\":\"t\",\"o\":1.1},{\"c\":\" \",\"o\":1.15},{\"c\":\"t\",\"o\":1.188},{\"c\":\"o\",\"o\":1.263},{\"c\":\" \",\"o\":1.325},{\"c\":\"t\",\"o\":1.35},{\"c\":\"e\",\"o\":1.4},{\"c\":\"a\",\"o\":1.45},{\"c\":\"s\",\"o\":1.512},{\"c\":\"e\",\"o\":1.575},{\"c\":\" \",\"o\":1.637},{\"c\":\"y\",\"o\":1.687},{\"c\":\"o\",\"o\":1.775},{\"c\":\"u\",\"o\":1.825},{\"c\":\",\",\"o\":1.9},{\"c\":\" \",\"o\":1.925},{\"c\":\"a\",\"o\":1.949},{\"c\":\"h\",\"o\":2.001}],\"x\":\"All red Lamb' just to tease you, ah\"},{\"ts\":26.666,\"te\":28.754,\"l\":[{\"c\":\"N\",\"o\":0},{\"c\":\"o\",\"o\":0},{\"c\":\"n\",\"o\":0.075},{\"c\":\"e\",\"o\":0.127},{\"c\":\" \",\"o\":0.163},{\"c\":\"o\",\"o\":0.176},{\"c\":\"f\",\"o\":0.201},{\"c\":\" \",\"o\":0.226},{\"c\":\"t\",\"o\":0.239},{\"c\":\"h\",\"o\":0.263},{\"c\":\"e\",\"o\":0.316},{\"c\":\"s\",\"o\":0.387},{\"c\":\"e\",\"o\":0.426},{\"c\":\" \",\"o\":0.476},{\"c\":\"t\",\"o\":0.501},{\"c\":\"o\",\"o\":0.538},{\"c\":\"y\",\"o\":0.599},{\"c\":\"s\",\"o\":0.663},{\"c\":\" \",\"o\":0.768},{\"c\":\"o\",\"o\":0.816},{\"c\":\"n\",\"o\":0.888},{\"c\":\" \",\"o\":0.991},{\"c\":\"l\",\"o\":1.063},{\"c\":\"e\",\"o\":1.115},{\"c\":\"a\",\"o\":1.186},{\"c\":\"s\",\"o\":1.273},{\"c\":\"e\",\"o\":1.361},{\"c\":\" \",\"o\":1.436},{\"c\":\"t\",\"o\":1.475},{\"c\":\"o\",\"o\":1.565},{\"c\":\"o\",\"o\":1.663},{\"c\":\",\",\"o\":1.775},{\"c\":\" \",\"o\":1.813},{\"c\":\"a\",\"o\":1.85},{\"c\":\"h\",\"o\":1.929}],\"x\":\"None of these toys on lease too, ah\"}]","lyrics_copyright":"Writer(s): Henry Walter, Guillaume de Homem-Christo, Martin Mckinney, Thomas Bangalter, Abel Tesfaye\nLyrics powered by www.musixmatch.com","richsync_length":233,"richsync_language":"en","richsync_language_description":"English","script_tracking_url":"http://tracking.musixmatch.com/t1.0/m_js/e_0/sn_0/l_0/su_6/tr_YslpI3lSWtY0cZG7Mjz64xQyTfhkeWDh4PnQ70MOfMT7-UrMbspN_3J2697kRvv2iD4psBvcJ1y3-5QDL-ETX_41cEP17LuiRSdiLghl_74Wwk7-bPGWoAaKCYFFnj0rJoQjhdXfxOdr-PNpFZksuFrLIawA5cp4PHtBRvhitm4J9NDdQDJm3m4yDalan9nsuloeykPm7I74NAtXw6gJaQUyPhWOJZ5cuRcZVypIfhib0xHOZPtXAclmrNV8ltH7H6S4U2fS0EPciPGZKBXuXctNPNMmEhlHwfan4n2bRjHNSeT1ZjEjRhwhTFE3oIoqJTnE_thOS69br08abRDGdK-fLSnreyg-QTXqjnSHGg4L2HJgsyhnPVUapKn6OFBV/","updated_time":"2017-02-08T13:34:47Z"}
             */

            private RichsyncBean richsync;

            public RichsyncBean getRichsync() {
                return richsync;
            }

            public void setRichsync(RichsyncBean richsync) {
                this.richsync = richsync;
            }

            public static class RichsyncBean {
                /**
                 * richsync_id : 6
                 * restricted : 0
                 * richsync_body : [{"ts":16.426,"te":18.261,"l":[{"c":"I","o":0},{"c":"'","o":0.013},{"c":"m","o":0.025},{"c":" ","o":0.088},{"c":"t","o":0.114},{"c":"r","o":0.137},{"c":"y","o":0.163},{"c":"n","o":0.213},{"c":"a","o":0.276},{"c":" ","o":0.299},{"c":"p","o":0.311},{"c":"u","o":0.348},{"c":"t","o":0.386},{"c":" ","o":0.399},{"c":"y","o":0.399},{"c":"o","o":0.424},{"c":"u","o":0.45},{"c":" ","o":0.489},{"c":"i","o":0.501},{"c":"n","o":0.512},{"c":" ","o":0.549},{"c":"t","o":0.562},{"c":"h","o":0.587},{"c":"e","o":0.626},{"c":" ","o":0.688},{"c":"w","o":0.7},{"c":"o","o":0.813},{"c":"r","o":0.888},{"c":"s","o":0.911},{"c":"t","o":0.963},{"c":" ","o":1.026},{"c":"m","o":1.076},{"c":"o","o":1.212},{"c":"o","o":1.288},{"c":"d","o":1.386},{"c":",","o":1.489},{"c":" ","o":1.512},{"c":"a","o":1.563},{"c":"h","o":1.65}],"x":"I'm tryna put you in the worst mood, ah"},{"ts":18.934,"te":21.218,"l":[{"c":"P","o":0},{"c":"1","o":0.013},{"c":" ","o":0.298},{"c":"c","o":0.4},{"c":"l","o":0.536},{"c":"e","o":0.575},{"c":"a","o":0.612},{"c":"n","o":0.65},{"c":"e","o":0.687},{"c":"r","o":0.786},{"c":" ","o":0.85},{"c":"t","o":0.875},{"c":"h","o":0.911},{"c":"a","o":0.991},{"c":"n","o":1.049},{"c":" ","o":1.112},{"c":"y","o":1.137},{"c":"o","o":1.188},{"c":"u","o":1.249},{"c":"r","o":1.287},{"c":" ","o":1.325},{"c":"c","o":1.338},{"c":"h","o":1.4},{"c":"u","o":1.474},{"c":"r","o":1.536},{"c":"c","o":1.575},{"c":"h","o":1.637},{"c":" ","o":1.702},{"c":"s","o":1.725},{"c":"h","o":1.8},{"c":"o","o":1.875},{"c":"e","o":1.914},{"c":"s","o":1.95},{"c":",","o":2},{"c":" ","o":2.025},{"c":"a","o":2.038},{"c":"h","o":2.062}],"x":"P1 cleaner than your church shoes, ah"},{"ts":21.441,"te":23.35,"l":[{"c":"M","o":0},{"c":"i","o":0.011},{"c":"l","o":0.05},{"c":"l","o":0.11},{"c":"i","o":0.198},{"c":" ","o":0.222},{"c":"p","o":0.248},{"c":"o","o":0.286},{"c":"i","o":0.324},{"c":"n","o":0.349},{"c":"t","o":0.374},{"c":" ","o":0.399},{"c":"t","o":0.412},{"c":"w","o":0.436},{"c":"o","o":0.474},{"c":" ","o":0.513},{"c":"j","o":0.525},{"c":"u","o":0.549},{"c":"s","o":0.601},{"c":"t","o":0.662},{"c":" ","o":0.762},{"c":"t","o":0.811},{"c":"o","o":0.864},{"c":" ","o":0.937},{"c":"h","o":0.961},{"c":"u","o":1.036},{"c":"r","o":1.1},{"c":"t","o":1.15},{"c":" ","o":1.187},{"c":"y","o":1.212},{"c":"o","o":1.275},{"c":"u","o":1.376},{"c":",","o":1.501},{"c":" ","o":1.565},{"c":"a","o":1.587},{"c":"h","o":1.675}],"x":"Milli point two just to hurt you, ah"},{"ts":23.949,"te":26.224,"l":[{"c":"A","o":0},{"c":"l","o":0.013},{"c":"l","o":0.075},{"c":" ","o":0.103},{"c":"r","o":0.15},{"c":"e","o":0.249},{"c":"d","o":0.402},{"c":" ","o":0.477},{"c":"L","o":0.512},{"c":"a","o":0.598},{"c":"m","o":0.661},{"c":"b","o":0.748},{"c":"'","o":0.853},{"c":" ","o":0.863},{"c":"j","o":0.887},{"c":"u","o":0.936},{"c":"s","o":1.05},{"c":"t","o":1.1},{"c":" ","o":1.15},{"c":"t","o":1.188},{"c":"o","o":1.263},{"c":" ","o":1.325},{"c":"t","o":1.35},{"c":"e","o":1.4},{"c":"a","o":1.45},{"c":"s","o":1.512},{"c":"e","o":1.575},{"c":" ","o":1.637},{"c":"y","o":1.687},{"c":"o","o":1.775},{"c":"u","o":1.825},{"c":",","o":1.9},{"c":" ","o":1.925},{"c":"a","o":1.949},{"c":"h","o":2.001}],"x":"All red Lamb' just to tease you, ah"},{"ts":26.666,"te":28.754,"l":[{"c":"N","o":0},{"c":"o","o":0},{"c":"n","o":0.075},{"c":"e","o":0.127},{"c":" ","o":0.163},{"c":"o","o":0.176},{"c":"f","o":0.201},{"c":" ","o":0.226},{"c":"t","o":0.239},{"c":"h","o":0.263},{"c":"e","o":0.316},{"c":"s","o":0.387},{"c":"e","o":0.426},{"c":" ","o":0.476},{"c":"t","o":0.501},{"c":"o","o":0.538},{"c":"y","o":0.599},{"c":"s","o":0.663},{"c":" ","o":0.768},{"c":"o","o":0.816},{"c":"n","o":0.888},{"c":" ","o":0.991},{"c":"l","o":1.063},{"c":"e","o":1.115},{"c":"a","o":1.186},{"c":"s","o":1.273},{"c":"e","o":1.361},{"c":" ","o":1.436},{"c":"t","o":1.475},{"c":"o","o":1.565},{"c":"o","o":1.663},{"c":",","o":1.775},{"c":" ","o":1.813},{"c":"a","o":1.85},{"c":"h","o":1.929}],"x":"None of these toys on lease too, ah"}]
                 * lyrics_copyright : Writer(s): Henry Walter, Guillaume de Homem-Christo, Martin Mckinney, Thomas Bangalter, Abel Tesfaye
                 Lyrics powered by www.musixmatch.com
                 * richsync_length : 233
                 * richsync_language : en
                 * richsync_language_description : English
                 * script_tracking_url : http://tracking.musixmatch.com/t1.0/m_js/e_0/sn_0/l_0/su_6/tr_YslpI3lSWtY0cZG7Mjz64xQyTfhkeWDh4PnQ70MOfMT7-UrMbspN_3J2697kRvv2iD4psBvcJ1y3-5QDL-ETX_41cEP17LuiRSdiLghl_74Wwk7-bPGWoAaKCYFFnj0rJoQjhdXfxOdr-PNpFZksuFrLIawA5cp4PHtBRvhitm4J9NDdQDJm3m4yDalan9nsuloeykPm7I74NAtXw6gJaQUyPhWOJZ5cuRcZVypIfhib0xHOZPtXAclmrNV8ltH7H6S4U2fS0EPciPGZKBXuXctNPNMmEhlHwfan4n2bRjHNSeT1ZjEjRhwhTFE3oIoqJTnE_thOS69br08abRDGdK-fLSnreyg-QTXqjnSHGg4L2HJgsyhnPVUapKn6OFBV/
                 * updated_time : 2017-02-08T13:34:47Z
                 */

                private int richsync_id;
                private int restricted;
                private String richsync_body;
                private String lyrics_copyright;
                private int richsync_length;
                private String richsync_language;
                private String richsync_language_description;
                private String script_tracking_url;
                private String updated_time;

                public int getRichsync_id() {
                    return richsync_id;
                }

                public void setRichsync_id(int richsync_id) {
                    this.richsync_id = richsync_id;
                }

                public int getRestricted() {
                    return restricted;
                }

                public void setRestricted(int restricted) {
                    this.restricted = restricted;
                }

                public String getRichsync_body() {
                    return richsync_body;
                }

                public void setRichsync_body(String richsync_body) {
                    this.richsync_body = richsync_body;
                }

                public String getLyrics_copyright() {
                    return lyrics_copyright;
                }

                public void setLyrics_copyright(String lyrics_copyright) {
                    this.lyrics_copyright = lyrics_copyright;
                }

                public int getRichsync_length() {
                    return richsync_length;
                }

                public void setRichsync_length(int richsync_length) {
                    this.richsync_length = richsync_length;
                }

                public String getRichsync_language() {
                    return richsync_language;
                }

                public void setRichsync_language(String richsync_language) {
                    this.richsync_language = richsync_language;
                }

                public String getRichsync_language_description() {
                    return richsync_language_description;
                }

                public void setRichsync_language_description(String richsync_language_description) {
                    this.richsync_language_description = richsync_language_description;
                }

                public String getScript_tracking_url() {
                    return script_tracking_url;
                }

                public void setScript_tracking_url(String script_tracking_url) {
                    this.script_tracking_url = script_tracking_url;
                }

                public String getUpdated_time() {
                    return updated_time;
                }

                public void setUpdated_time(String updated_time) {
                    this.updated_time = updated_time;
                }
            }
        }
    }
}
