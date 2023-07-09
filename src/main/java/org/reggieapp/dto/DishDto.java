package org.reggieapp.dto;

import lombok.Data;
import org.reggieapp.entity.Dish;
import org.reggieapp.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
