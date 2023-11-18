package com.raveendra.foodapp.data.network.firebase.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class FirebaseAuthDataSourceImplTest {

    @MockK
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dataSource: FirebaseAuthDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
    }

    @Test
    fun doLogin() {
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuthResult()
        runTest {
            val result = dataSource.doLogin("email", "password")
            assertEquals(result, true)
            verify { firebaseAuth.signInWithEmailAndPassword(any(), any()) }
        }
    }

    @Test
    fun doRegister() {
        runTest {
            mockkConstructor(UserProfileChangeRequest.Builder::class)
            every { anyConstructed<UserProfileChangeRequest.Builder>().build() } returns mockk()

            val mockAuthResult = mockTaskAuthResult()
            every { firebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns mockAuthResult

            val mockUser = mockk<FirebaseUser>(relaxed = true)
            every { mockAuthResult.result.user } returns mockUser
            every { mockUser.updateProfile(any()) } returns mockTaskVoid()

            val result = dataSource.doRegister("fullname", "email", "password")
            assertEquals(result, true)
        }
    }

    @Test
    fun doUpdateProfile() {
        runTest {
            mockkConstructor(UserProfileChangeRequest.Builder::class)
            every { anyConstructed<UserProfileChangeRequest.Builder>().build() } returns mockk()

            val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
            every { firebaseAuth.currentUser } returns mockFirebaseUser

            val mockTaskVoid = mockTaskVoid()
            every { mockFirebaseUser.updateProfile(any()) } returns mockTaskVoid

            val result = dataSource.updateProfile("fullname", photoUri = null)
            assertEquals(result, true)
        }
    }

    @Test
    fun updatePassword() {
        runTest {
            val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
            every { firebaseAuth.currentUser } returns mockFirebaseUser

            val mockTaskVoid = mockTaskVoid()
            every { mockFirebaseUser.updatePassword(any()) } returns mockTaskVoid

            val result = dataSource.updatePassword("newPassword")
            assertEquals(result, true)
        }
    }

    @Test
    fun updateEmail() {
        runTest {
            val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
            every { firebaseAuth.currentUser } returns mockFirebaseUser

            val mockTaskVoid = mockTaskVoid()
            every { mockFirebaseUser.updateEmail(any()) } returns mockTaskVoid

            val result = dataSource.updateEmail("newEmail")
            assertEquals(result, true)
        }
    }

    @Test
    fun sendChangePasswordRequestByEmail() {
        runTest {
            val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
            every { firebaseAuth.currentUser } returns mockFirebaseUser

            val mockTaskVoid = mockTaskVoid()
            every { firebaseAuth.sendPasswordResetEmail(any()) } returns mockTaskVoid

            val result = dataSource.sendChangePasswordRequestByEmail()
            assertEquals(result, true)
        }
    }

    @Test
    fun getCurrentUser() {
        val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
        every { firebaseAuth.currentUser } returns mockFirebaseUser
        val result = dataSource.getCurrentUser()
        assertEquals(result, mockFirebaseUser)
    }

    @Test
    fun doLogout() {
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuth
        every { firebaseAuth.signOut() } returns Unit
        val result = dataSource.doLogout()
        assertEquals(result, true)
    }

    @Test
    fun isLoggedIn() {
        val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)
        every { firebaseAuth.currentUser } returns mockFirebaseUser
        val result = dataSource.isLoggedIn()
        assertEquals(result, true)
    }

    private fun mockTaskVoid(exception: Exception? = null): Task<Void> {
        val task: Task<Void> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false
        val relaxedVoid: Void = mockk(relaxed = true)
        every { task.result } returns relaxedVoid
        return task
    }

    private fun mockTaskAuthResult(exception: Exception? = null): Task<AuthResult> {
        val task: Task<AuthResult> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false
        val relaxedVoid: AuthResult = mockk(relaxed = true)
        every { task.result } returns relaxedVoid
        every { task.result.user } returns mockk(
            relaxed = true
        )
        return task
    }
}
