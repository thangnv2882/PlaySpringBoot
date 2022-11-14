package com.example.handlefile.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "segments")
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSegment;

    private String text;

//    link to table run
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "segment")
    @JsonIgnore
    private List<Run> runs = new ArrayList<>();

//    link to table document
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private Document document;

}
