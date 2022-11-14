package com.example.handlefile.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.docx4j.wml.UnderlineEnumeration;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "runs")
public class Run {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRun;

    private String text;

    private boolean isBold = false;
    private UnderlineEnumeration underlineEnumeration;
    private boolean isStrike = false;
    private boolean isItalic = false;

//    link to table segment
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "segment_id")
    private Segment segment;

}
