package org.soloviova.liudmyla.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"role"})
@ToString(exclude = {"role"})
public class PlayerItem {
    private Integer id;
    private String screenName;
    private String gender;
    private Integer age;
    private String role;
}
