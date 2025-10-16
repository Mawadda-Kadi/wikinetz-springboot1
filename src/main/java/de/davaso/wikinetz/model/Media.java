package de.davaso.wikinetz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer mediaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @ToString.Exclude
    private Article article;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filepath;

    @Enumerated(EnumType.STRING)
    private MediaType type;
}
