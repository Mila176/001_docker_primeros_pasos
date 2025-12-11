package com.pucetec.products.services

import com.pucetec.products.exceptions.ProductAlreadyExistsException
import com.pucetec.products.exceptions.ProductNotFoundException
import com.pucetec.products.exceptions.StockOutOfRangeException
import com.pucetec.products.mappers.ProductMapper
import com.pucetec.products.models.entities.Product
import com.pucetec.products.models.requests.ProductRequest
import com.pucetec.products.repositories.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductServiceTest {

    private lateinit var productRepositoryMock: ProductRepository
    private lateinit var productMapperMock: ProductMapper

    private lateinit var productService: ProductService

    /***
     * TODO TEST TIENE UN CICLO DE VIDA
     *
     * BEFORE ->
     *  @beforeall -> ejecutarse antes de todo los test
     *  @beforeCach -> ejecutarse antes de cada test
     *
     *  TEST
     *  @Test
     *
     * AFTER
     * @AlterAll -> ejecutarse despues de todos los test
     * @AlterCach -> ejecutarse despues de cada test
     *
     */

    @BeforeEach
    fun init(){
        productRepositoryMock = mock(ProductRepository::class.java)
        productMapperMock = ProductMapper()

        productService = ProductService(
            productRepository = productRepositoryMock,
            productMapper = productMapperMock
        )
    }

    /**
     * Sintaxis de un test
     * 1. el test deberia o no deberia hacer algo
     * should - shouldn't
     *
     * 2. el test debe ejecutar una funcion de la clase objetivo
     * el nombre de la funcion
     *
     * should return a product given a valid id
     *
     * 3. el test debe contemplar el input
     * should return a product EVEN a valid id
     */
    @Test
    fun `SHOULD return a product response GIVEN a valid id`(){

        val productId = 1L

        val mockProduct = Product(
            name = "agua",
            price = 0.5,
            stock = 10
        ).apply{
            id = productId
        }

        `when`(productRepositoryMock.findById(productId))
            .thenReturn(Optional.of(mockProduct))

        val response = productService.findById(productId)

        assertEquals(mockProduct.name, response.name)
        assertEquals(mockProduct.id,response.id)
        assertEquals(mockProduct.price, response.price)
        assertEquals(mockProduct.stock, response.stock)


    }

    @Test
    fun `SHOULD return a ProductNotFoundException GIVEN a non existing product id`(){

        `when`(productRepositoryMock.findById(88L))
            .thenReturn(Optional.empty())

        assertThrows<ProductNotFoundException>{
            productService.findById(88L)
        }
    }

    /**
     * COVERAGE
     *
     * coverage -> numero de lineas que estan cubiertas
     * por test en un archivo
     *
     */

    @Test
    fun `SHOULD save a product GIVEN a valid product request`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val productToSaved = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id = 1L }

        //hay que mockear el findByName
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(null)
        //hay que mockear el save
        `when`(productRepositoryMock.save(savedProduct))
            .thenReturn(savedProduct)

        val response = productService.save(request)

        assertEquals(savedProduct.id, response.id)
    }

    @Test
    fun `SHOULD NOT save a product GIVEN a existing product name`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val productToSaved = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id = 1L }

        //hay que mockear el findByName
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(savedProduct)
        //hay que mockear el save
        `when`(productRepositoryMock.save(savedProduct))
            .thenReturn(savedProduct)

        assertThrows<ProductAlreadyExistsException>{
            productService.save(request)
        }
    }

    @Test
    fun `SHOULD NOT save a product GIVEN a stock equal or bigger than 10`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 11
        )

        assertThrows<StockOutOfRangeException>{
            productService.save(request)
        }
    }


}
