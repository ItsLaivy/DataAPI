package codes.laivy.dataapi.modules.type;

import java.io.IOException;
import java.io.Serializable;

public abstract class DataAPISerializable implements Serializable {

    protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        onDeserialize();
    }

    public abstract void onDeserialize();

}
