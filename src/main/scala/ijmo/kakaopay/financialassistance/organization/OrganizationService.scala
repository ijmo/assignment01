package ijmo.kakaopay.financialassistance.organization

import org.springframework.stereotype.Service

@Service
class OrganizationService (val organizationRepository: OrganizationRepository){
  def findByName(name: String): Option[Organization] = organizationRepository.findByName(name)

  def addOrganization(organization: Organization): Organization = organizationRepository.save(organization)

  def findOrAddOrganization(name: String): Organization = findByName(name).getOrElse(addOrganization(Organization(name)))
}
