package Data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Unmarshalling {
    private ArrayList<String> properties;
    private HashMap<String, TYPE> propToValue;
    public Unmarshalling(){
        properties = new ArrayList<>();
        propToValue = new HashMap<>();
    }
    public Unmarshalling defineComponents(Unmarshalling unpacker){
        if(unpacker!=null){
            properties.addAll(unpacker.properties);
            propToValue.putAll(unpacker.propToValue);
        }
        return this;
    }
    public UnmarshalledMSG parseByteArray(byte[] data){
        int offset = 0;
        HashMap<String, Object> map = new HashMap<>();
        try{
            for(String property: properties){
                TYPE value = propToValue.get(property);
                switch(value){
                    case INTEGER:
                        map.put(property, parseInt(data, offset));
                        offset+=4;
                        break;
                    case DOUBLE:
                        map.put(property, parseDouble(data,offset));
                        offset+=8;
                        break;
                    case STRING:
                        int length = parseInt(data, offset);
                        map.put(property, parseString(data, offset + 4, length));
                        offset += 4 + length;
                        break;
                    case BYTE_ARRAY:
                        int byte_length = parseInt(data, offset);
                        map.put(property, Arrays.copyOfRange(data, offset + 4, offset + 4 + byte_length));
                        break;
                    case ONE_BYTE_INT:
                        map.put(property, new OneByteInt(data[offset] & 0xFF));
                        offset += 1;
                        break;
                }
            }
            UnmarshalledMSG result = new UnmarshalledMSG(map);
            return result;
        }catch(Exception e){
            return null;
        }
    }
    public enum TYPE {
        INTEGER, DOUBLE, STRING, BYTE_ARRAY, ONE_BYTE_INT
    }
    private String parseString(byte[] data, int offset, int length) {
        try{
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<length;i++,offset++){
                sb.append((char)data[offset]);
            }
            return sb.toString();
        }catch(IndexOutOfBoundsException e){
            return null;
        }

    }
    private Double parseDouble(byte[] data, int offset) {
        int doubleSize = 8;
        byte[] temp = new byte[doubleSize];
        for(int i =0;i<doubleSize;i++){
            temp[i] = data[offset+i];
        }
        double value = ByteBuffer.wrap(temp).getDouble();
        return value;
    }
    private Integer parseInt(byte[] data, int offset) {
        int intSize = 4;
        byte[] temp = new byte[intSize];
        for(int i=0;i<intSize;i++){
            temp[i] = data[offset+i];
        }

        int value = ByteBuffer.wrap(temp).getInt();
        return value;
    }
    public static class UnmarshalledMSG{
        private HashMap<String, Object> map;

        /**
         * Class constructor of UnpackedMsg
         * @param map
         */
        public UnmarshalledMSG(HashMap<String,Object> map){
            this.map = map;
        }

        public Integer getInteger(String key){
            if(map.containsKey(key) && (map.get(key) instanceof Integer)){
                return (Integer) map.get(key);
            }
            return null;
        }
        public String getString(String key){
            if(map.containsKey(key) && map.get(key) instanceof String){
                return (String) map.get(key);
            }
            return null;
        }
        public Double getDouble(String key){
            if(map.containsKey(key) && map.get(key) instanceof Double){
                return (Double) map.get(key);
            }
            return null;
        }
        public byte[] getByteArray(String value) {
            if (map.containsKey(value) && map.get(value) instanceof byte[]) {
                return (byte[]) map.get(value);
            }
            return null;
        }

        public OneByteInt getOneByteInt(String value) {
            if (map.containsKey(value) && map.get(value) instanceof OneByteInt) {
                return (OneByteInt) map.get(value);
            }
            return null;
        }
    }
    public static class Builder{
        private Unmarshalling unmarshaller;
        public Builder(){
            unmarshaller = new Unmarshalling();

        }
        public Builder setType(String property, TYPE type){
            unmarshaller.properties.add(property);
            unmarshaller.propToValue.put(property, type);
            return this;
        }
        public Unmarshalling build(){
            return unmarshaller;
        }
    }
}
