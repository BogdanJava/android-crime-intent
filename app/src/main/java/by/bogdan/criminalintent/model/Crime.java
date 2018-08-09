package by.bogdan.criminalintent.model;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@NoArgsConstructor
@Data
public class Crime {
    private UUID mId = UUID.randomUUID();
    private String mTitle;
    private Date mDate = new Date();
    private boolean mSolved;
}
