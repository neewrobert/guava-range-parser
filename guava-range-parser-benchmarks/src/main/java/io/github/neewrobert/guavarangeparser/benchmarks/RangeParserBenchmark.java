package io.github.neewrobert.guavarangeparser.benchmarks;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(2)
@State(Scope.Benchmark)
public class RangeParserBenchmark {

  private RangeParser parser;
  private RangeParser lenientParser;

  @Setup
  public void setup() {
    parser = RangeParser.builder().build();
    lenientParser = RangeParser.builder().lenient(true).build();
  }

  // --- Integer parsing ---

  @Benchmark
  public Range<Integer> parseIntegerClosed() {
    return parser.parseRange("[0..100]", Integer.class);
  }

  @Benchmark
  public Range<Integer> parseIntegerClosedOpen() {
    return parser.parseRange("[0..100)", Integer.class);
  }

  @Benchmark
  public Range<Integer> parseIntegerOpen() {
    return parser.parseRange("(0..100)", Integer.class);
  }

  // --- Unbounded ranges ---

  @Benchmark
  public Range<Integer> parseIntegerAtLeast() {
    return parser.parseRange("[0..+∞)", Integer.class);
  }

  @Benchmark
  public Range<Integer> parseIntegerAtMost() {
    return parser.parseRange("(-∞..100]", Integer.class);
  }

  @Benchmark
  public Range<Integer> parseIntegerAll() {
    return parser.parseRange("(-∞..+∞)", Integer.class);
  }

  // --- Other numeric types ---

  @Benchmark
  public Range<Long> parseLongClosed() {
    return parser.parseRange("[0..9999999999]", Long.class);
  }

  @Benchmark
  public Range<Double> parseDoubleClosed() {
    return parser.parseRange("[0.5..1.5]", Double.class);
  }

  // --- Temporal types ---

  @Benchmark
  public Range<LocalDate> parseLocalDateClosed() {
    return parser.parseRange("[2024-01-01..2024-12-31]", LocalDate.class);
  }

  @Benchmark
  public Range<Duration> parseDurationClosedOpen() {
    return parser.parseRange("[PT1H..PT24H)", Duration.class);
  }

  // --- String type ---

  @Benchmark
  public Range<String> parseStringClosed() {
    return parser.parseRange("[a..z]", String.class);
  }

  // --- Negative numbers ---

  @Benchmark
  public Range<Integer> parseNegativeNumbers() {
    return parser.parseRange("[-100..-10]", Integer.class);
  }

  // --- Lenient mode ---

  @Benchmark
  public Range<Integer> parseLenientBracketless() {
    return lenientParser.parseRange("0..100", Integer.class);
  }

  @Benchmark
  public Range<Integer> parseLenientWithBrackets() {
    return lenientParser.parseRange("[0..100]", Integer.class);
  }

  // --- Static method (delegates to DEFAULT_INSTANCE, measures static dispatch overhead) ---

  @Benchmark
  public Range<Integer> parseStaticMethod() {
    return RangeParser.parse("[0..100)", Integer.class);
  }

  // --- Whitespace handling ---

  @Benchmark
  public Range<Integer> parseWithWhitespace() {
    return parser.parseRange("  [ 0 .. 100 )  ", Integer.class);
  }
}
