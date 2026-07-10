package Helpers;

import Errors.FileFormatException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MiniJson {
    private MiniJson(){}

    public static Object Parse(String text){
        Parser p = new Parser(text);
        p.skipWhitespace();
        Object value = p.parseValue();
        p.skipWhitespace();
        if(!p.atEnd())
            throw new FileFormatException("Unexpected trailing content in JSON file at position "+p.pos+".");
        return value;
    }

    public static String EscapeString(String s){
        StringBuilder sb = new StringBuilder();
        for(char c: s.toCharArray()){
            switch (c){
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static class Parser {
        private final String text;
        private int pos = 0;

        Parser(String text){ this.text = text; }

        boolean atEnd(){ return pos>=text.length(); }

        void skipWhitespace(){
            while(pos<text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
        }

        char peek(){
            if(atEnd()) throw new FileFormatException("Unexpected end of JSON file.");
            return text.charAt(pos);
        }

        void expect(char c){
            if(atEnd() || text.charAt(pos)!=c)
                throw new FileFormatException("Expected '"+c+"' at position "+pos+" in JSON file.");
            pos++;
        }

        Object parseValue(){
            skipWhitespace();
            char c = peek();
            if(c=='{') return parseObject();
            if(c=='[') return parseArray();
            if(c=='"') return parseString();
            if(c=='t' || c=='f') return parseBoolean();
            if(c=='n'){ expectLiteral("null"); return null; }
            return parseNumber();
        }

        Map<String,Object> parseObject(){
            Map<String,Object> map = new LinkedHashMap<>();
            expect('{');
            skipWhitespace();
            if(peek()=='}'){ pos++; return map; }
            while(true){
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = peek();
                if(c==','){ pos++; continue; }
                if(c=='}'){ pos++; break; }
                throw new FileFormatException("Expected ',' or '}' at position "+pos+" in JSON file.");
            }
            return map;
        }

        List<Object> parseArray(){
            List<Object> list = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if(peek()==']'){ pos++; return list; }
            while(true){
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = peek();
                if(c==','){ pos++; continue; }
                if(c==']'){ pos++; break; }
                throw new FileFormatException("Expected ',' or ']' at position "+pos+" in JSON file.");
            }
            return list;
        }

        String parseString(){
            expect('"');
            StringBuilder sb = new StringBuilder();
            while(true){
                if(atEnd()) throw new FileFormatException("Unterminated string in JSON file.");
                char c = text.charAt(pos++);
                if(c=='"') break;
                if(c=='\\'){
                    if(atEnd()) throw new FileFormatException("Unterminated escape sequence in JSON file.");
                    char esc = text.charAt(pos++);
                    switch (esc){
                        case '"'  -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/'  -> sb.append('/');
                        case 'n'  -> sb.append('\n');
                        case 't'  -> sb.append('\t');
                        case 'r'  -> sb.append('\r');
                        case 'u'  -> {
                            if(pos+4>text.length()) throw new FileFormatException("Invalid unicode escape in JSON file.");
                            sb.append((char)Integer.parseInt(text.substring(pos,pos+4),16));
                            pos += 4;
                        }
                        default -> throw new FileFormatException("Invalid escape sequence '\\"+esc+"' in JSON file.");
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Boolean parseBoolean(){
            if(text.startsWith("true", pos)){ pos+=4; return Boolean.TRUE; }
            if(text.startsWith("false", pos)){ pos+=5; return Boolean.FALSE; }
            throw new FileFormatException("Invalid literal at position "+pos+" in JSON file.");
        }

        void expectLiteral(String literal){
            if(!text.startsWith(literal, pos))
                throw new FileFormatException("Invalid literal at position "+pos+" in JSON file.");
            pos += literal.length();
        }

        Double parseNumber(){
            int start = pos;
            if(!atEnd() && text.charAt(pos)=='-') pos++;
            while(!atEnd() && Character.isDigit(text.charAt(pos))) pos++;
            if(!atEnd() && text.charAt(pos)=='.'){
                pos++;
                while(!atEnd() && Character.isDigit(text.charAt(pos))) pos++;
            }
            if(!atEnd() && (text.charAt(pos)=='e'||text.charAt(pos)=='E')){
                pos++;
                if(!atEnd() && (text.charAt(pos)=='+'||text.charAt(pos)=='-')) pos++;
                while(!atEnd() && Character.isDigit(text.charAt(pos))) pos++;
            }
            if(pos==start) throw new FileFormatException("Invalid number at position "+pos+" in JSON file.");
            try {
                return Double.parseDouble(text.substring(start,pos));
            } catch (NumberFormatException ex){
                throw new FileFormatException("Invalid number at position "+pos+" in JSON file.");
            }
        }
    }
}
