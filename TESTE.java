public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        boolean isMinOptionsEnabled = minOptionsToBeRanked != Const.POINTS_NO_VALUE;
        boolean isMaxOptionsEnabled = maxOptionsToBeRanked != Const.POINTS_NO_VALUE;

        Set<Integer> responseRank = new HashSet<>();
        for (FeedbackResponseDetails response : responses) {
            FeedbackRankRecipientsResponseDetails details = (FeedbackRankRecipientsResponseDetails) response;

            if (responseRank.contains(details.getAnswer()) && !areDuplicatesAllowed) {
                errors.add("Duplicate rank " + details.getAnswer() + " in question");
            } else if (details.getAnswer() < 1) {
                errors.add("Invalid rank " + details.getAnswer() + " in question");
            }
            responseRank.add(details.getAnswer());
        }
        // if number of options ranked is less than the minimum required trigger this
        // error
        if (isMinOptionsEnabled && responses.size() < minOptionsToBeRanked) {
            errors.add("You must rank at least " + minOptionsToBeRanked + " options.");
        }
        // if number of options ranked is more than the maximum possible trigger this
        // error
        if (isMaxOptionsEnabled && responses.size() > maxOptionsToBeRanked) {
            errors.add("You can rank at most " + maxOptionsToBeRanked + " options.");
        }

        return errors;
    }

    @Test
    public void testValidateResponsesDetails_InvalidRank() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails = new FeedbackContributionQuestionDetails();
        List<FeedbackResponseDetails> responses = new ArrayList<>();

        // Adiciona uma resposta com rank inválido (menor que 1)
        FeedbackContributionResponseDetails invalidResponse = new FeedbackContributionResponseDetails();
        invalidResponse.setAnswer(0); // Defina um valor menor que 1 para o teste
        responses.add(invalidResponse);

        // Executa a validação e verifica se a lista de erros contém a mensagem de erro esperada
        List<String> errors = feedbackContributionQuestionDetails.validateResponsesDetails(responses, 1);
        assertFalse(errors.isEmpty()); // A lista de erros não deve estar vazia
        assertTrue(errors.get(0).contains("Invalid rank 0 in question")); // A mensagem de erro deve estar presente
    }


    @Test
    public void testValidateResponseDetails_duplicateRankOptions_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3"));
        List<String> errorResponse = new ArrayList<>();

        FeedbackRankOptionsResponseDetails feedbackResponseDetails = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails.setAnswers(Arrays.asList(1, 1));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_DUPLICATE_RANK_RESPONSE);
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));
    }