package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.loan_application.LoanApplicationPage;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ListLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    public Mono<LoanApplicationPageList> execute(Long status, int page, int size) {
        return loanApplicationRepository.getFilterList(status, page, size)
                .flatMap(list -> {
                    List<String> emails = list.getLoanApplications()
                            .stream()
                            .map(LoanApplicationPage::getEmail)
                            .distinct()
                            .toList();

                    return userRepository.findAllByEmails(emails)
                            .map(users -> {
                                list.getLoanApplications().forEach(loanApplicationPage ->
                                        users.stream()
                                                .filter(u -> u.getEmail().equalsIgnoreCase(loanApplicationPage.getEmail()))
                                                .findFirst()
                                                .ifPresent(loanApplicationPage::setUser)
                                );
                                return list;
                            });
                });
    }



}
