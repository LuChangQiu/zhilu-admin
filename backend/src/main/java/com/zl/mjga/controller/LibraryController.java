package com.zl.mjga.controller;

import com.zl.mjga.dto.knowledge.DocUpdateDto;
import com.zl.mjga.dto.knowledge.LibraryUpsertDto;
import com.zl.mjga.repository.LibraryDocRepository;
import com.zl.mjga.repository.LibraryDocSegmentRepository;
import com.zl.mjga.repository.LibraryRepository;
import com.zl.mjga.service.RagService;
import com.zl.mjga.service.UploadService;
import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.mjga.tables.pojos.Library;
import org.jooq.generated.mjga.tables.pojos.LibraryDoc;
import org.jooq.generated.mjga.tables.pojos.LibraryDocSegment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
@Slf4j
public class LibraryController {

  private final UploadService uploadService;
  private final RagService ragService;
  private final LibraryRepository libraryRepository;
  private final LibraryDocRepository libraryDocRepository;
  private final LibraryDocSegmentRepository libraryDocSegmentRepository;

  @GetMapping("/libraries")
  public List<Library> queryLibraries() {
    return libraryRepository.findAll().stream().sorted(
            Comparator.comparing(Library::getId).reversed()
    ).toList();
  }

  @GetMapping("/docs")
  public List<LibraryDoc> queryLibraryDocs(@RequestParam Long libraryId) {
    return libraryDocRepository.fetchByLibId(libraryId).stream().sorted(
            Comparator.comparing(LibraryDoc::getId).reversed()
    ).toList();
  }

  @GetMapping("/segments")
  public List<LibraryDocSegment> queryLibraryDocSegments(@RequestParam Long libraryDocId) {
    return libraryDocSegmentRepository.fetchByDocId(libraryDocId);
  }

  @PostMapping("/library")
  public void upsertLibrary(@RequestBody @Valid LibraryUpsertDto libraryUpsertDto) {
    Library library = new Library();
    library.setId(libraryUpsertDto.id());
    library.setName(libraryUpsertDto.name());
    library.setDescription(libraryUpsertDto.description());
    libraryRepository.merge(library);
  }

  @DeleteMapping("/library")
  public void deleteLibrary(@RequestParam Long libraryId) {
    ragService.deleteLibraryBy(libraryId);
  }

  @DeleteMapping("/doc")
  public void deleteLibraryDoc(@RequestParam Long libraryDocId) {
    ragService.deleteDocBy(libraryDocId);
  }

  @PutMapping("/doc")
  public void updateLibraryDoc(@RequestBody @Valid DocUpdateDto docUpdateDto) {
    LibraryDoc exist = libraryDocRepository.fetchOneById(docUpdateDto.id());
    exist.setEnable(docUpdateDto.enable());
    libraryDocRepository.merge(exist);
  }

  @PostMapping(value = "/doc/upload", produces = MediaType.TEXT_PLAIN_VALUE)
  public String uploadLibraryDoc(
      @RequestPart("libraryId") String libraryId, @RequestPart("file") MultipartFile multipartFile)
      throws Exception {
    String objectName = uploadService.uploadLibraryDoc(multipartFile);
    Long libraryDocId =
        ragService.createLibraryDocBy(
            Long.valueOf(libraryId), objectName, multipartFile.getOriginalFilename());
    ragService.embeddingAndCreateDocSegment(libraryDocId, objectName);
    return objectName;
  }
}
