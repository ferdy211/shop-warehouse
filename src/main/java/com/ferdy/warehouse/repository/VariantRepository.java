package com.ferdy.warehouse.repository;

import com.ferdy.warehouse.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findByItemId(Long itemId);

    Optional<Variant> findByIdAndItemId(Long id, Long itemId);

    boolean existsByItemId(Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("update Variant v set v.stock = v.stock - :quantity where v.id = :id and v.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}
