package com.mauriciogiordano.commit.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauricio on 10/16/14.
 */
public class BaseModel {

    protected BaseModelListenerHandler baseModelListenerHandler;

    public static abstract class OnUpdateListener {
        public abstract void onUpdate(BaseModel object);
    }

    protected static class BaseModelListenerHandler {

        public List<OnUpdateListener> onUpdateListeners;

        private static BaseModelListenerHandler baseModelListenerHandler = null;

        public static BaseModelListenerHandler getInstance() {
            if(baseModelListenerHandler == null) {
                baseModelListenerHandler = new BaseModelListenerHandler();
            }

            return baseModelListenerHandler;
        }

        private BaseModelListenerHandler() {
            onUpdateListeners = new ArrayList<OnUpdateListener>();
        }

        protected void execOnUpdateListeners(BaseModel target) {
            int size = onUpdateListeners.size();

            for(int i = 0; i < size; i++) {
                onUpdateListeners.get(i).onUpdate(target);
            }
        }
    }

    public BaseModel() {
        baseModelListenerHandler = BaseModelListenerHandler.getInstance();
    }

    public void addOnUpdateListener(OnUpdateListener onUpdateListener) {
        baseModelListenerHandler.onUpdateListeners.add(onUpdateListener);
    }

    public void removeOnUpdateListener(OnUpdateListener onUpdateListener) {
        baseModelListenerHandler.onUpdateListeners.remove(onUpdateListener);
    }
}
