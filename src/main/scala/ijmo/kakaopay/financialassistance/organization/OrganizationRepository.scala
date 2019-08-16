package ijmo.kakaopay.financialassistance.organization

import org.springframework.data.jpa.repository.JpaRepository

trait OrganizationRepository extends JpaRepository[Organization, java.lang.String]{
  def findAll(): java.util.List[Organization]
  def findByName(name: String): Option[Organization]
}
