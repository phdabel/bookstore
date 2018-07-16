package abelcorrea.com.br.bookstore.db;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;


public abstract class BaseModel implements Serializable {

    private static final String LOG_TAG = BaseModel.class.getSimpleName();

    public long id;

    /**
     * Returns a JSON Object of the model.
     *
     * @return
     */
    public JSONObject toJSON(){

        JSONObject result = new JSONObject();

        for(Field field : getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                Object obj = field.get(this);

                //null objects just continue the loop
                if(obj == null) continue;

                if(obj.getClass().isArray()){
                    int arraySize = Array.getLength(obj);
                    JSONArray jsonArray = new JSONArray();
                    for(int i = 0; i < arraySize; i++){
                        Object innerObject = Array.get(obj, i);
                        if(innerObject instanceof BaseModel){
                            JSONObject innerJson = ((BaseModel) innerObject).toJSON();
                            jsonArray.put(innerJson);
                        }else{
                            jsonArray.put(innerObject);
                        }
                    }
                    result.put(field.getName(), jsonArray);
                }else if(obj instanceof BaseModel){
                    JSONObject innerJson = ((BaseModel) obj).toJSON();
                    result.put(field.getName(), innerJson);
                }else result.put(field.getName(), obj);

            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.toString());
                e.printStackTrace();
            }
        }
        result.remove("serialVersionUID");
        return result;

    }


    /**
     * Generic toString method.
     *
     * @return name of the class and its content.
     */
    public String toString(){

        StringBuilder result = new StringBuilder();

        result.append(getClass().getSimpleName()).append("=");
        result.append("{\"id\":").append("\"").append(String.valueOf(this.id)).append("\",");
        result.append(this.toJSON().toString());
        return result.toString().replaceFirst(",\\{",",");
    }

}
