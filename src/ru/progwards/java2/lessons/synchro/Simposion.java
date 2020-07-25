package ru.progwards.java2.lessons.synchro;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Simposion {
    final int NUMBER_OF_PHILOSOPHERS = 5;
    final int NUMBER_OF_FORKS = 5;
    Map<Integer, Philosopher> philosophers;

    // инициализирует необходимое количество философов и вилок. Каждый философ выполняется в отдельном потоке
    // reflectTime задает время в мс, через которое философ проголодается
    // eatTime задает время в мс, через которое получив 2 вилки философ наестся и положит вилки на место
    Simposion(long reflectTime, long eatTime){
        philosophers = new HashMap<>();
        initPhilosophers(reflectTime, eatTime);
        initForks();
    }

    private void initPhilosophers(long reflectTime, long eatTime){
        Semaphore semaphore = new Semaphore(NUMBER_OF_FORKS / 2);
        for (int i=0; i<NUMBER_OF_PHILOSOPHERS; i++){
            String name = "Philosopher_" + i;
            Philosopher philosopher = new Philosopher(name, reflectTime, eatTime, semaphore);
            philosophers.put(i, philosopher);
        }
    }

    private void initForks(){
        if (NUMBER_OF_PHILOSOPHERS == NUMBER_OF_FORKS * 2){
            //
        } else if (NUMBER_OF_PHILOSOPHERS == NUMBER_OF_FORKS){
            for (int i=0; i<NUMBER_OF_FORKS; i++){
                Fork fork = new Fork();
                philosophers.get(i).setRight(fork);
                if (i == NUMBER_OF_PHILOSOPHERS-1)
                    philosophers.get(0).setLeft(fork);
                else
                    philosophers.get(i+1).setLeft(fork);
            }
        } else if (NUMBER_OF_PHILOSOPHERS < NUMBER_OF_FORKS){
            throw new RuntimeException("Not enough forks");
        } else {
            //
        }
    }

    // запускает философскую беседу
    void start(){
        philosophers.values().forEach(Thread::start);
    }
    // завершает философскую беседу
    void stop(){
        philosophers.values().forEach(Thread::interrupt);
    }
    void print(){
        // Философ name, ел ххх, размышлял xxx
        philosophers.values().forEach(ph -> System.out.println("Философ " + ph.name + ", ел " + ph.eatSum + ", размышлял " + ph.reflectSum));
    }

    public static void main(String[] args) throws InterruptedException {
        Simposion simposion = new Simposion(100, 100);
        simposion.start();
        Thread.sleep(10000);
        simposion.stop();
        simposion.print();
    }
}
