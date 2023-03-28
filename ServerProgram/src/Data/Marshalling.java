package Data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Marshalling {

    private ArrayList<String> properties;
    private HashMap<String, Object> propToValue;

    public Marshalling(){
        properties = new ArrayList<>();
        propToValue = new HashMap<>();
    }
    public void setValue(String key, Object value){
        properties.add(key);
        propToValue.put(key,value);
    }
    public HashMap<String,Object> getPropToValue(){
        return this.propToValue;
    }

    public byte[] getByteArray(){
        int size = 1;
        for(Object value: propToValue.values()){
            if(value instanceof Integer){
                size+=4;
            }
            else if(value instanceof String){
                size += 4 + ((String) value).length();
            }
            else if (value instanceof Double){
                size+= 8;
            }
            else if(value instanceof byte[]){
                size+= 4 + ((byte[]) value).length;
            }
            else if(value instanceof OneByteInt){
                size +=1;
            }
        }
        byte[] buffer = new byte[size];

        int index = 0;
        for(String property: properties){
            Object value = propToValue.get(property);
            if(value instanceof Integer){
                index = intToByte((Integer)value, buffer, index); /*Converts Integer to byte*/
            }
            else if(value instanceof String){
                index = intToByte(((String) value).length(), buffer, index); /*1st integer is length of string*/
                index = stringToByte(((String)value), buffer,index);		/*Convert the content of string to bytes*/
            }
            else if(value instanceof Double){
                index = doubleToByte((Double)value,buffer,index);		/*Convert double to bytes*/
            }
            else if(value instanceof byte[]){
                index = intToByte(((byte[]) value).length, buffer,index); /*First int is length of byte array*/
                System.arraycopy(value, 0, buffer, index, ((byte[])value).length); /*Copy contents of byte array into buffer*/
                index+= ((byte[]) value).length;
            }else if(value instanceof OneByteInt){
                int OBIValue = ((OneByteInt) value).getValue();
                buffer[index++] = (byte) (OBIValue & 0xFF);
            }

        }

        return buffer;
    }

    private int intToByte(int i, byte[] buffer, int index){

        byte[] temp = new byte[4];
        ByteBuffer.wrap(temp).putInt(i);
        for(byte b: temp){
            buffer[index++] = b;
        }

        return index;
    }
    private int stringToByte(String s, byte[] buffer, int index){
        for(byte b: s.getBytes()){
            buffer[index++] = b;
        }
        return index;
    }
    private int doubleToByte(Double d, byte[] buffer, int index){
        byte[] temp = new byte[8];
        ByteBuffer.wrap(temp).putDouble(d);
        for(byte b: temp){
            buffer[index++] = b;
        }
        return index;
    }
    public static class Builder{
        private Marshalling marshaller;

        public Builder(){
            marshaller = new Marshalling();
        }

        public Builder setProperty(String key, int value){
            return set(key,value);
        }

        public Builder setProperty(String key, double value){
            return set(key, value);
        }

        public Builder setProperty(String key, String string){
            return set(key, string);
        }

        public Builder setProperty(String key, OneByteInt value){
            return set(key,value);
        }

        public Builder set(String key, Object value){
            marshaller.setValue(key,value);
            return this;
        }


        public Marshalling build(){
            return marshaller;
        }
    }
}
