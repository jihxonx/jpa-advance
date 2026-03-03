package org.example.jpaadvance.repository;

import org.example.jpaadvance.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

}
