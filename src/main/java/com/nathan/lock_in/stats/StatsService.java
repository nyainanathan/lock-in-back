package com.nathan.lock_in.stats;

import com.nathan.lock_in.chronos.Chronos;
import com.nathan.lock_in.chronos.ChronosService;
import com.nathan.lock_in.chronos.DurationUnit;
import com.nathan.lock_in.user.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ChronosService chronoService;
    private final StatsRepository statsRepository;

    public GlobalStats getGlobalStats(StatsRangeEnum range) throws Exception {

        List<Chronos> chronos;
        LocalDate todayDate = LocalDate.now();

        if(range == null){
            //Fetch all time chronos for this user
            chronos = chronoService.getChronosBetweenDates(null, null);

        } else{

            LocalDate startDate = todayDate;

            if (range == StatsRangeEnum.week){

                //Getting the amount of days     
                while(!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                    startDate = startDate.minusDays(1);
                }

            } else if (range == StatsRangeEnum.month){

                //fetch the last month stats
                startDate.minusMonths(1);

            }

            String startDateTime = startDate + "T00:00:00.000Z";

            chronos = chronoService.getChronosBetweenDates(Instant.parse(startDateTime), null);
        }

        GlobalStats stats = new GlobalStats();
        stats.setSessionsCount(chronos.size());

        double totalFocusedMinutes = 0;
        double totalPausedMinutes = 0;
        
        for(Chronos chrono : chronos){

            double focusForThisSession;

            if(chrono.getUnit() == DurationUnit.hour){
                focusForThisSession = chrono.getDuration() * 60;
            } else if(chrono.getUnit() == DurationUnit.minute){
                focusForThisSession = chrono.getDuration();
            } else {
                focusForThisSession = chrono.getDuration() / 60;
            }

            totalFocusedMinutes += focusForThisSession;

            double sessionTotal  = Duration.between(
                chrono.getStoppedAt(), chrono.getCreatedAt()
            ).toMinutes();

            totalPausedMinutes += sessionTotal - focusForThisSession;

        }

        stats.setFocusedMinutes(totalFocusedMinutes);
        stats.setPause(totalPausedMinutes);
        stats.setFocusRatio(totalFocusedMinutes/totalPausedMinutes);
        stats.setAverageSessionsMinutes(
            totalFocusedMinutes / chronos.size()
        );

        return stats;
    }

    public FocusTrends[] getTrends(Integer dayRange){
        if(dayRange == null){
            dayRange = 7;
        }

        FocusTrends[] trends = new FocusTrends[dayRange];
        LocalDate startDate = LocalDate.now();

        for(int i = 0 ; i  < dayRange ; i++){
            startDate = startDate.minusDays(1);
            trends[dayRange - 1 - i] = new FocusTrends(startDate, 0d);
        }

        String startDateTime = startDate.toString() + "T00:00:00.000Z";

        List<FocusTrends> actualTrends = statsRepository.getTrendsInRange(startDateTime, getUserId());
        
        for(FocusTrends t : trends){
            for(FocusTrends actualTrend : actualTrends){
                if(actualTrend.getDate().equals(t.getDate())){
                    t.setFocusedMinutes(
                        actualTrend.getFocusedMinutes()
                    );
                }
            }
        }

        return trends;
    }

    public List<ProjectStats> getProjectStats(){
        return statsRepository.getStatsPerProject(getUserId());
    }

    public StreakStats getUserStreakStats() {
        return statsRepository.getUserStreak(getUserId());
    }

    private String getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return user.getId();
    }
}
