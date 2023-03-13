package com.rosevii.dto;

import com.rosevii.domain.Setmeal;
import com.rosevii.domain.SetmealDish;
import lombok.Data;

import java.util.List;


@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
