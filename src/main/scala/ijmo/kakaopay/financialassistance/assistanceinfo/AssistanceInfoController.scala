package ijmo.kakaopay.financialassistance.assistanceinfo

import java.net.URI

import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.search.{SearchQueryDTO, SearchService}
import javax.validation.Valid
import org.springframework.data.domain.{PageRequest, Pageable}
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._

@RestController
@RequestMapping(Array("/api/assistanceinfo"))
class AssistanceInfoController (val assistanceInfoService: AssistanceInfoService,
                                val searchService: SearchService) {
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array(""))
  def list(): java.lang.Iterable[AssistanceInfoDTO] = {
    val infos = assistanceInfoService.findAll()
    infos.map(AssistanceInfoDTO(_)).asJava
  }

  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array("/find"))
  def list(@RequestParam limit: Int): java.lang.Iterable[String] = {
    val pageNum = 0
    val pageable: Pageable = PageRequest.of(pageNum, limit)
    assistanceInfoService.findOrganizationNames(pageable).asJava
  }

  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array("/minimumRate"))
  def listMinimumRate(): java.lang.Iterable[String] = {
    assistanceInfoService.findOrganizationNamesWithMinimumRate().asJava
  }

  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array("/match"))
  def get(@RequestBody assistanceInfoDTO: AssistanceInfoDTO): ResponseEntity[Object] = {
    if (assistanceInfoDTO.region == null || assistanceInfoDTO.region.trim == "") {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val result = assistanceInfoService.findByOrganizationName(assistanceInfoDTO.region)
    if (result == null) return new ResponseEntity(HttpStatus.NOT_FOUND)
    new ResponseEntity(AssistanceInfoDTO(result), HttpStatus.OK)
  }

  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array("/search"))
  def search(@Valid @RequestBody query: SearchQueryDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val assistanceInfo = searchService.searchByText(query.input)
    if (assistanceInfo == null) return new ResponseEntity(HttpStatus.NOT_FOUND)
    val m: Map[String, String] = Map(
      "region" -> assistanceInfo.organization.code,
      "usage" -> assistanceInfo.usages.split(",").mkString(" 및 "),
      "limit" -> assistanceInfo.maxAmount,
      "rate" -> Numbers.rates(assistanceInfo.rate1, assistanceInfo.rate2))
    new ResponseEntity(m.asJava, HttpStatus.OK)
  }

  @PreAuthorize("#oauth2.hasScope('write') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array(""))
  def create(@Valid @RequestBody assistanceInfoDTO: AssistanceInfoDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val assistanceInfo = assistanceInfoService.addAssistanceInfo(assistanceInfoDTO)
    val headers: HttpHeaders = new HttpHeaders()
    headers.setLocation(URI.create("/api/assistanceinfo/" + assistanceInfo.id))
    new ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("#oauth2.hasScope('write') and #oauth2.hasScope('assistanceInfo')")
  @PutMapping(Array("/{id}"))
  def update(@PathVariable id: Long, @Valid @RequestBody assistanceInfoDTO: AssistanceInfoDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val assistanceInfo = assistanceInfoService.findById(id).orNull
    if (assistanceInfo == null) return new ResponseEntity(HttpStatus.NOT_FOUND)

    assistanceInfoService.updateAssistanceInfo(assistanceInfo, assistanceInfoDTO)
    val headers: HttpHeaders = new HttpHeaders()
    headers.setLocation(URI.create("/api/assistanceinfo/" + assistanceInfo.id))
    new ResponseEntity(HttpStatus.ACCEPTED)
  }
}
