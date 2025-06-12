package com.tecsup.demo.content.model;

import com.tecsup.demo.authentication.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_material")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "material_id"})
    })
    public static class FavoriteMaterialConstraints {}
}
