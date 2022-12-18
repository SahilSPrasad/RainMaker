package rainmaker;

import java.util.ArrayList;

public interface Observer {
    void updateObserver(double windSpeed);
}

class WindSubject implements Subject {
    private ArrayList<Observer> observers;
    private double windSpeed;

    public WindSubject() {
        observers = new ArrayList<>();
    }

    @Override
    public void register(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.updateObserver(windSpeed);
        }
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
        notifyObserver();
    }
}

