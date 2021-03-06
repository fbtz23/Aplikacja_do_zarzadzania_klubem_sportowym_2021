package com.bbsoftware.SportClub.announcement;

import com.bbsoftware.SportClub.appuser.AppUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Announcement implements Comparable<Announcement>{

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    private AppUser user;

    Date date;

    @Override
    public int compareTo(Announcement o) {
        return this.getId().compareTo(o.getId());
    }
}
