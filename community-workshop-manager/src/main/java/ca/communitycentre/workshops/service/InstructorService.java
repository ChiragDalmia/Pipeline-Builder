package ca.communitycentre.workshops.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.RecordNotFoundException;
import ca.communitycentre.workshops.form.InstructorForm;
import ca.communitycentre.workshops.repository.InstructorRepository;

@Service
@Transactional(readOnly = true)
public class InstructorService {

    static final String DUPLICATE_EMAIL = "Another instructor already uses this email address.";

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAllByOrderByFullNameAsc();
    }

    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Instructor", id));
    }

    @Transactional
    public Instructor create(InstructorForm form) {
        String email = TextNormalizer.email(form.getEmail());
        if (instructorRepository.existsByEmail(email)) {
            throw new DuplicateValueException("email", DUPLICATE_EMAIL);
        }
        Instructor instructor = new Instructor(
                TextNormalizer.clean(form.getFullName()), email, TextNormalizer.clean(form.getSpeciality()));
        // Flush now so a constraint failure is raised inside this call, not later.
        return instructorRepository.saveAndFlush(instructor);
    }

    @Transactional
    public Instructor update(Long id, InstructorForm form) {
        Instructor instructor = findById(id);
        String email = TextNormalizer.email(form.getEmail());
        // Exclude the record being edited, so keeping the same email is allowed.
        if (instructorRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateValueException("email", DUPLICATE_EMAIL);
        }
        instructor.setFullName(TextNormalizer.clean(form.getFullName()));
        instructor.setEmail(email);
        instructor.setSpeciality(TextNormalizer.clean(form.getSpeciality()));
        instructorRepository.flush();
        return instructor;
    }
}
