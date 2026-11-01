package ca.communitycentre.workshops.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.InvalidInstructorException;
import ca.communitycentre.workshops.exception.RecordNotFoundException;
import ca.communitycentre.workshops.form.WorkshopForm;
import ca.communitycentre.workshops.projection.WorkshopOverview;
import ca.communitycentre.workshops.repository.InstructorRepository;
import ca.communitycentre.workshops.repository.WorkshopRepository;

@Service
@Transactional(readOnly = true)
public class WorkshopService {

    static final String DUPLICATE_CODE = "Another workshop already uses this code.";

    private final WorkshopRepository workshopRepository;
    private final InstructorRepository instructorRepository;

    public WorkshopService(WorkshopRepository workshopRepository, InstructorRepository instructorRepository) {
        this.workshopRepository = workshopRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<Workshop> findAll() {
        return workshopRepository.findAllByOrderByScheduledDateAscCodeAsc();
    }

    public Workshop findById(Long id) {
        return workshopRepository.findWithInstructorById(id)
                .orElseThrow(() -> new RecordNotFoundException("Workshop", id));
    }

    /** Rows from the JPQL inner join, used by the overview page. */
    public List<WorkshopOverview> getOverview() {
        return instructorRepository.findWorkshopOverview();
    }

    @Transactional
    public Workshop create(WorkshopForm form) {
        String code = TextNormalizer.code(form.getCode());
        if (workshopRepository.existsByCode(code)) {
            throw new DuplicateValueException("code", DUPLICATE_CODE);
        }
        Instructor instructor = requireInstructor(form.getInstructorId());
        Workshop workshop = new Workshop(code, TextNormalizer.clean(form.getTitle()), form.getScheduledDate(),
                form.getDurationMinutes(), form.getCapacity(), instructor);
        return workshopRepository.saveAndFlush(workshop);
    }

    @Transactional
    public Workshop update(Long id, WorkshopForm form) {
        Workshop workshop = findById(id);
        String code = TextNormalizer.code(form.getCode());
        if (workshopRepository.existsByCodeAndIdNot(code, id)) {
            throw new DuplicateValueException("code", DUPLICATE_CODE);
        }
        Instructor instructor = requireInstructor(form.getInstructorId());
        workshop.setCode(code);
        workshop.setTitle(TextNormalizer.clean(form.getTitle()));
        workshop.setScheduledDate(form.getScheduledDate());
        workshop.setDurationMinutes(form.getDurationMinutes());
        workshop.setCapacity(form.getCapacity());
        workshop.setInstructor(instructor);
        workshopRepository.flush();
        return workshop;
    }

    private Instructor requireInstructor(Long instructorId) {
        if (instructorId == null) {
            throw new InvalidInstructorException(null);
        }
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InvalidInstructorException(instructorId));
    }
}
