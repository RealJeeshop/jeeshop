package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestMailTemplate;
import org.rembx.jeeshop.user.test.TestUser;
import sun.security.acl.PrincipalImpl;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Date;
import java.util.List;

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
                new MailTemplateFinder(entityManager), mailerMock, sessionContextMock);
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
    public void find() throws Exception {
        assertThat(service.find(1L)).isNotNull();
    }

    @Test
    public void create_shouldThrowBadRequestExWhenUserIdIsNotNull() throws Exception{

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
    public void create_shouldOnlyPersistUserWhenOperationIsTriggeredByAdminUser() throws Exception{

        User user = new User("register2@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");
        Principal principal = new PrincipalImpl(JeeshopRoles.ADMIN);
        when(sessionContextMock.getCallerPrincipal()).thenReturn(principal);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        verify(sessionContextMock,times(2)).getCallerPrincipal();

        assertThat(entityManager.find(User.class, user.getId())).isNotNull();

        entityManager.remove(user);
    }

    @Test
    public void create_shouldPersistEndUserAndRetrieveUserRegistrationMailTemplateAndSendMail() throws Exception{

        User user = new User("register3@test.com", "test", "John", "Doe", "+33616161616",null,new Date(),"fr_FR",null);
        user.setGender("M.");

        Principal principal = new PrincipalImpl(JeeshopRoles.USER);
        when(sessionContextMock.getCallerPrincipal()).thenReturn(principal);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        verify(sessionContextMock,times(2)).getCallerPrincipal();
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

        Principal principal = new PrincipalImpl(JeeshopRoles.USER);
        when(sessionContextMock.getCallerPrincipal()).thenReturn(principal);

        entityManager.getTransaction().begin();
        service.create(user);
        entityManager.getTransaction().commit();

        doThrow(new IllegalStateException("Test Exception")).when(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(),user.getLogin(),testMailTemplate.userRegistrationMailTemplate().getContent());

        verify(sessionContextMock,times(2)).getCallerPrincipal();
        verify(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(),user.getLogin(),"<html><body>Welcome M. John Doe</body></html>");

        assertThat(entityManager.find(User.class, user.getId())).isNotNull();
        entityManager.remove(user);
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