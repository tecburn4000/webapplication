package com.example.webapplication.service.ftp;

import com.example.webapplication.dto.ftp.BreadcrumbDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FtpBreadcrumbService}.
 * <p>
 * This test class verifies the correct generation of breadcrumb navigation
 * for various FTP path inputs, including edge cases.
 *
 * <p><b>Test structure:</b>
 * <ul>
 *     <li>Organized using {@code @Nested} classes for better readability</li>
 *     <li>Parameterized tests for standard scenarios</li>
 *     <li>Dedicated tests for edge cases</li>
 * </ul>
 *
 * <p><b>Covered cases:</b>
 * <ul>
 *     <li>Root path ("/")</li>
 *     <li>Single-level and multi-level paths</li>
 *     <li>Paths with redundant slashes</li>
 *     <li>Empty and blank inputs</li>
 *     <li>Null input handling</li>
 *     <li>Trailing slashes</li>
 * </ul>
 */
class FtpBreadcrumbServiceTest {

    private final FtpBreadcrumbService service = new FtpBreadcrumbService();

    @Nested
    class StandardCases {

        /**
         * Verifies breadcrumb creation for common valid paths.
         *
         * @param input    the input FTP path
         * @param expected the expected breadcrumb list
         */
        @ParameterizedTest(name = "[{index}] Path: \"{0}\"")
        @MethodSource("com.example.webapplication.service.ftp.FtpBreadcrumbServiceTest#breadcrumbTestCases")
        @DisplayName("Should create correct breadcrumbs for standard paths")
        void shouldCreateBreadcrumbs(String input, List<BreadcrumbDto> expected) {

            List<BreadcrumbDto> result = service.breadcrumbs(input);

            assertThat(result)
                    .hasSize(expected.size())
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }

    @Nested
    class EdgeCases {

        /**
         * Verifies that a null input is handled gracefully.
         * <p>
         * Expected behavior: return root breadcrumb only.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Should handle null input")
        void shouldHandleNullInput() {
            List<BreadcrumbDto> result = service.breadcrumbs(null);

            assertThat(result)
                    .containsExactly(new BreadcrumbDto("root", "/"));
        }

        /**
         * Verifies handling of blank input strings.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Should handle blank input")
        void shouldHandleBlankInput() {
            List<BreadcrumbDto> result = service.breadcrumbs("   ");

            assertThat(result)
                    .containsExactly(new BreadcrumbDto("root", "/"));
        }

        /**
         * Verifies handling of trailing slashes.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Should handle trailing slashes")
        void shouldHandleTrailingSlashes() {
            List<BreadcrumbDto> result = service.breadcrumbs("/folder/subfolder/");

            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BreadcrumbDto("root", "/"),
                            new BreadcrumbDto("folder", "/folder"),
                            new BreadcrumbDto("subfolder", "/folder/subfolder")
                    ));
        }
    }

    /**
     * Provides test cases for standard breadcrumb generation.
     *
     * @return a stream of test arguments
     */
    static Stream<org.junit.jupiter.params.provider.Arguments> breadcrumbTestCases() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        "/",
                        List.of(new BreadcrumbDto("root", "/"))
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "/folder",
                        List.of(
                                new BreadcrumbDto("root", "/"),
                                new BreadcrumbDto("folder", "/folder")
                        )
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "/folder/subfolder",
                        List.of(
                                new BreadcrumbDto("root", "/"),
                                new BreadcrumbDto("folder", "/folder"),
                                new BreadcrumbDto("subfolder", "/folder/subfolder")
                        )
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "//folder///subfolder",
                        List.of(
                                new BreadcrumbDto("root", "/"),
                                new BreadcrumbDto("folder", "/folder"),
                                new BreadcrumbDto("subfolder", "/folder/subfolder")
                        )
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "",
                        List.of(new BreadcrumbDto("root", "/"))
                )
        );
    }
}