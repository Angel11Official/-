package org.reggieapp.dto;

import org.reggieapp.entity.Setmeal;
import org.reggieapp.entity.SetmealDish;
import lombok.Data;
import org.reggieapp.entity.Setmeal;
import org.reggieapp.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
