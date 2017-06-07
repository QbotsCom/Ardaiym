package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class Stock {
    private int id;
    private String name;
    private String description;
    private String date;
    private List<String> typeOfWork;
    private List<ParticipantOfStock> participantOfStocks;

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ParticipantOfStock> getParticipantOfStocks() {
        return participantOfStocks;
    }

    public void setParticipantOfStocks(List<ParticipantOfStock> participantOfStocks) {
        this.participantOfStocks = participantOfStocks;
    }

    public void addUser(ParticipantOfStock participantOfStock){
        participantOfStocks.add(participantOfStock);
    }

    public List<String> getTypeOfWork() {
        return typeOfWork;
    }

    public void addTypeOfWork(String typeOfWork){
        if (this.typeOfWork == null){
            this.typeOfWork = new ArrayList<>();
        }
        this.typeOfWork.add(typeOfWork);

    }

    public void setTypeOfWork(List<String> typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    @Override
    public String toString() {
        return "<b>" + name + "</b>\n\n" + description + "\n\n" + date;
    }
}
