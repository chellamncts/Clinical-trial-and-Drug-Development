package com.genc.ctds.crcBackend.model;

import java.util.List;

public class CrcDashboard {

    private int scheduledVisits;
    private int pendingCrfs;
    private int completedCrfs;
    private int lockedCrfs;
    private int openQueries;
    private int enrolledSubjects;

    private List<String> visitsTimelineLabels;
    private List<Integer> visitsTimelineData;
    private List<Integer> subjectEnrollmentData;

    public int getScheduledVisits() { return scheduledVisits; }
    public void setScheduledVisits(int scheduledVisits) { this.scheduledVisits = scheduledVisits; }

    public int getPendingCrfs() { return pendingCrfs; }
    public void setPendingCrfs(int pendingCrfs) { this.pendingCrfs = pendingCrfs; }

    public int getCompletedCrfs() { return completedCrfs; }
    public void setCompletedCrfs(int completedCrfs) { this.completedCrfs = completedCrfs; }

    public int getLockedCrfs() { return lockedCrfs; }
    public void setLockedCrfs(int lockedCrfs) { this.lockedCrfs = lockedCrfs; }

    public int getOpenQueries() { return openQueries; }
    public void setOpenQueries(int openQueries) { this.openQueries = openQueries; }

    public int getEnrolledSubjects() { return enrolledSubjects; }
    public void setEnrolledSubjects(int enrolledSubjects) { this.enrolledSubjects = enrolledSubjects; }

    public List<String> getVisitsTimelineLabels() { return visitsTimelineLabels; }
    public void setVisitsTimelineLabels(List<String> visitsTimelineLabels) { this.visitsTimelineLabels = visitsTimelineLabels; }

    public List<Integer> getVisitsTimelineData() { return visitsTimelineData; }
    public void setVisitsTimelineData(List<Integer> visitsTimelineData) { this.visitsTimelineData = visitsTimelineData; }

    public List<Integer> getSubjectEnrollmentData() { return subjectEnrollmentData; }
    public void setSubjectEnrollmentData(List<Integer> subjectEnrollmentData) { this.subjectEnrollmentData = subjectEnrollmentData; }

}

