/*
package com.spring.ollama.controller;

public class ResumeControllerv1 {
    private final ResumeIndexService indexService;
    private final ResumeSearchService searchService;
    private final ResumeScoringService scoringService;

    public ResumeController(ResumeIndexService indexService,
                            ResumeSearchService searchService,
                            ResumeScoringService scoringService) {
        this.indexService = indexService;
        this.searchService = searchService;
        this.scoringService = scoringService;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws Exception {
        String text = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines().collect(Collectors.joining(" "));

        indexService.indexResume(file.getOriginalFilename(), text);
        return "Indexed";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String jobDescription) {

        List<Document> topResumes = searchService.search(jobDescription);

        return scoringService.score(jobDescription, topResumes);
    }
}
*/
