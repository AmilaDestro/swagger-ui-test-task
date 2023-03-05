package org.soloviova.liudmyla.entities;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Player {
    private Integer id;
    private String login;
    private String password;
    private String screenName;
    private String gender;
    private Integer age;
    private String role;
}
