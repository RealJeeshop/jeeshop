package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestMailTemplate;
import org.rembx.jeeshop.user.test.TestUser;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


public class UsersIT {

    private Users service;

    private TestUser testUser;
    private TestMailTemplate testMailTemplate;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private Mailer mailerMock;
    private SessionContext sessionContextMock;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testUser = TestUser.getInstance();
        testMailTemplate = TestMailTemplate.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        mailerMock = mock(Mailer.class);
        sessionContextMock = mock(SessionContext.class);
        service = new Users(entityManager, new UserFinder(entityManager), new RoleFinder(entityManager),
                new CountryChecker("FRA,GBR"),new MailTemplateFinder(entityManager), mailerMock, sessionContextMock);
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<User> users = service.findAll(null, 0, 1);
        assertThat(users).isNotEmpty();
        assertThat(users).containsExactly(testUser.firstUser());
    }

    @Test
    public void findAll_ByLogin_shouldReturnSearchedUser() {
        List<User> users = service.findAll(testUser.firstUser().getLogin(), 0, 1);
        assertThat(users).isNotEmpty();
        assertThat(users).containsExactly(testUser.firstUser());
    }


    @Test
    public void findAll_ByFirstName_shouldReturnSearchedUser() {
        List<User> users = service.findAll(testUser.firstUser().getFirstname(), 0, 1);
        assertThat(users).isNotEmpty();
        assertThat(users).containsExactly(testUser.firstUser());
    }

    @Test
    public void findAll_ByLastName_shouldReturnSearchedUser() {
        List<User> users = service.findAll(testUser.firstUser().getLastname(), 0, 1);
        assertThat(users).isNotEmpty();
        assertThat(users).containsExactly(testUser.firstUser());
    }

    @Test
    public void find() throws Exception {
        assertThat(service.find(1L)).isNotNull();
    }

    @Test
    public void find_withUnknownId_ShouldThrowException() throws Exception {
        try {
            service.find(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void count(){
        assertThat(service.count(null)).isGreaterThan(0);
    }


    @Test
    public void count_withUnknownSearchCriteria(){
        assertThat(service.count("unknown")).isEqualTo(0);
    }

    @Test
    public void create_shouldThrowBadRequestExWhenUserHasAnId() throws Exception{

        User user = new User();
        user.setId(777L);

        try {
            service.create(user);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    public void create_shouldThrowConflictExWhenUserWithGivenLoginAlreadyExists() throws Exception{

        User user = new User();
        user.setLogin("test@test.com");

        try {
            service.create(user);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.CONFLICT.getStatusCode());
        }
    }

    @Test
    public void create_shouldThrowBadRequestExceptionExWhenUsersCountryIsNotAvailable() throws Exception{

        User user = new User();
        user.setLogin("toto@toto.com");
        Address address = new Address();
        address.setCountryIso3Code("ZZZ");
        user.setAddress(address);

        try {
            service.create(user);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    public void create_shouldThrowBadRequestExceptionExWhenUserAddressHasAnId() throws Exception{

        User user = new User();
        user.setLogin("toto@toto.com");
        Address address = new Address();
        address.setId(1L);
        user.setAddress(address);

        try {
            service.create(user);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    public void create_shouldOnlyPersistUserWhenOperationIsTriggeredByAdminUser() throws Exception{

        User user = new User("register2@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");
        when(sessionContextMock.isCallerInRole(JeeshopRoles.ADMIN)).thenReturn(true);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isCallerInRole(JeeshopRoles.ADMIN);

        assertThat(entityManager.find(User.class, user.getId())).isNotNull();

        entityManager.remove(user);
    }

    @Test
    public void create_shouldPersistEndUserAndRetrieveUserRegistrationMailTemplateAndSendMail() throws Exception{

        User user = new User("register3@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");

        when(sessionContextMock.isCallerInRole(JeeshopRoles.ADMIN)).thenReturn(false);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isCallerInRole(JeeshopRoles.ADMIN);
        verify(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(), user.getLogin(), "<html><body>Welcome M. John Doe</body></html>");

        assertThat(entityManager.find(User.class, user.getId())).isNotNull();
        assertThat(user.getActivated()).isFalse();
        assertThat(user.getActionToken()).isNotNull();

        entityManager.remove(user);
    }

    @Test
    public void create_shouldJustPersistEndUserEvenWhenExceptionDuringMailSending() throws Exception{

        User user = new User("register1@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");

        Address address = new Address("7 blue street", "Nowhere", "00001", "John", "Doe","M.",null, "FRA");

        user.setAddress(address);

        when(sessionContextMock.isCallerInRole(JeeshopRoles.ADMIN)).thenReturn(false);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        doThrow(new IllegalStateException("Test Exception")).when(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(),user.getLogin(),testMailTemplate.userRegistrationMailTemplate().getContent());

        verify(sessionContextMock).isCallerInRole(JeeshopRoles.ADMIN);
        verify(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(),user.getLogin(),"<html><body>Welcome M. John Doe</body></html>");

        final User persistedUser = entityManager.find(User.class, user.getId());
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.getAddress()).isEqualTo(address);

        entityManager.remove(user);
    }

    @Test
    public void activate_shouldActivateUserAndClearActionToken() throws Exception{

        User user = new User("activate1@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");

        final UUID actionToken = UUID.randomUUID();
        user.setActionToken(actionToken);
        user.setActivated(false);

        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        service.activate(user.getLogin(), actionToken.toString());

        final User modifiedUser = entityManager.find(User.class, user.getId());
        assertThat(modifiedUser).isNotNull();
        assertThat(modifiedUser.getActivated()).isTrue();
        assertThat(modifiedUser.getActionToken()).isNull();

    }


    @Test
    public void activate_shouldThrowNotFoundExWhenUserIsNotFound() throws Exception{;

        try {
            service.activate("unknown_login", UUID.randomUUID().toString());
            fail("should have thrown ex");
        }catch(WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }

    }


    @Test
    public void sendResetPasswordMail_shouldGenerateActionTokenAndSendResetPasswordMail() throws Exception{

        entityManager.getTransaction().begin();
        service.sendResetPasswordMail(testUser.firstUser().getLogin());
        entityManager.getTransaction().commit();

        verify(mailerMock).sendMail(testMailTemplate.resetPasswordMailTemplate().getSubject(), testUser.firstUser().getLogin(), "<html><body>Here is the link to reset your password</body></html>");

        User persistedUser = entityManager.find(User.class, testUser.firstUser().getId());
        assertThat(persistedUser).isNotNull();

        entityManager.getTransaction().begin();
        persistedUser.setActionToken(null);
        entityManager.getTransaction().commit();

    }

    @Test
    public void sendResetPasswordMail_shouldThrowNotFoundEX_WhenUserIsNotFound() throws Exception{

        try {
            service.sendResetPasswordMail("unknown_login");
            fail("should have thrown ex");
        }catch(WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }

    }

    @Test
    public void resetPassword_shouldUpdateUserPasswordWhenActionTokenMatchesUserActionToken() throws Exception{

        User user = new User("reset1@test.com", "previousPassword", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");

        final UUID actionToken = UUID.randomUUID();
        user.setActionToken(actionToken);
        user.setActivated(false);

        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        service.resetPassword(user.getLogin(), actionToken.toString(),"newPassword");

        final User updatedUser = entityManager.find(User.class, user.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getActionToken()).isNotEqualTo("previousPassword");
        assertThat(updatedUser.getActionToken()).isNull();

    }

    @Test
    public void resetPassword_shouldThrowNotFoundEX_WhenUserIsNotFound() throws Exception{

        try {
            service.resetPassword("unknown_login",null,null);
            fail("should have thrown ex");
        }catch(WebApplicationException e){
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }

    }

    @Test
    public void modifyUser_ShouldModifyUser() {
        User detachedUserToModify = new User("test2@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);

        detachedUserToModify.setId(testUser.firstUser().getId());

        service.modify(detachedUserToModify);

    }

    @Test
    public void modifyUnknown_ShouldThrowNotFoundException() {
        User detachedUser = new User("test3@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);

        detachedUser.setId(9999L);
        try {
            service.modify(detachedUser);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void delete_shouldRemove(){
        entityManager.getTransaction().begin();
        User user = new User("test4@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(user.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(User.class, user.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx(){
        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    // TODO
    public void testFindCurrentUser() throws Exception {
    }
}