package com.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Step {

        private Stop departureStop;
        private Stop arrivalStop;
        private double cost;
}
