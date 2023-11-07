import com.sebastiangrupo.studios.papidate;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class PAPIDate extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "papidate";
    }

    @Override
    public String getAuthor() {
        return "Sebastian Software Studios";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String onPlaceholderRequest(String identifier) {

        if (identifier.equals("world_date")) {
            World world = Bukkit.getWorld("world");

            if (world != null) {
                long worldTime = world.getFullTime();

                long totalDays = worldTime / 24000L;
                int year = (int) (totalDays / 365);
                int dayOfYear = (int) (totalDays % 365) + 1;

                if (year < 0) {
                    year--;
                }

                String formattedDate = String.format("%02d/%02d/%02d", dayOfYear, (year % 100), year);

                return formattedDate;
            }
        }

        return null;
    }
}

