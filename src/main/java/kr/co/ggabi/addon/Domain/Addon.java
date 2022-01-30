package kr.co.ggabi.addon.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String userName;

    @Column(nullable = false)
    public String mailId;

    @Column(nullable = false)
    public String fileName;

    @Column(nullable = false)
    public double malicious;

    @Builder
    public Addon(String userName, String mailId, String fileName, double malicious){
        this.userName = userName;
        this.mailId = mailId;
        this.fileName = fileName;
        this.malicious = malicious;
    }

}
