package io.github.neewrobert.guavarangeparser.benchmarks;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeFormatter;
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
public class RangeFormatterBenchmark {

  private RangeFormatter formatter;

  private Range<Integer> intClosed;
  private Range<Integer> intClosedOpen;
  private Range<Integer> intOpen;
  private Range<Integer> intAtLeast;
  private Range<Integer> intAtMost;
  private Range<Integer> intAll;
  private Range<Integer> intNegative;
  private Range<Long> longClosed;
  private Range<Double> doubleClosed;
  private Range<LocalDate> dateClosed;
  private Range<Duration> durationClosedOpen;
  private Range<String> stringClosed;

  @Setup
  public void setup() {
    formatter = RangeFormatter.builder().build();

    intClosed = Range.closed(0, 100);
    intClosedOpen = Range.closedOpen(0, 100);
    intOpen = Range.open(0, 100);
    intAtLeast = Range.atLeast(0);
    intAtMost = Range.atMost(100);
    intAll = Range.all();
    intNegative = Range.closed(-100, -10);
    longClosed = Range.closed(0L, 9999999999L);
    doubleClosed = Range.closed(0.5, 1.5);
    dateClosed = Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
    durationClosedOpen = Range.closedOpen(Duration.ofHours(1), Duration.ofHours(24));
    stringClosed = Range.closed("a", "z");
  }

  // --- Integer formatting ---

  @Benchmark
  public String formatIntegerClosed() {
    return formatter.format(intClosed);
  }

  @Benchmark
  public String formatIntegerClosedOpen() {
    return formatter.format(intClosedOpen);
  }

  @Benchmark
  public String formatIntegerOpen() {
    return formatter.format(intOpen);
  }

  // --- Unbounded ranges ---

  @Benchmark
  public String formatIntegerAtLeast() {
    return formatter.format(intAtLeast);
  }

  @Benchmark
  public String formatIntegerAtMost() {
    return formatter.format(intAtMost);
  }

  @Benchmark
  public String formatIntegerAll() {
    return formatter.format(intAll);
  }

  // --- Other types ---

  @Benchmark
  public String formatLongClosed() {
    return formatter.format(longClosed);
  }

  @Benchmark
  public String formatDoubleClosed() {
    return formatter.format(doubleClosed);
  }

  @Benchmark
  public String formatLocalDateClosed() {
    return formatter.format(dateClosed);
  }

  @Benchmark
  public String formatDurationClosedOpen() {
    return formatter.format(durationClosedOpen);
  }

  @Benchmark
  public String formatStringClosed() {
    return formatter.format(stringClosed);
  }

  // --- Negative numbers ---

  @Benchmark
  public String formatNegativeNumbers() {
    return formatter.format(intNegative);
  }

  // --- Static method ---

  @Benchmark
  public String formatStaticMethod() {
    return RangeFormatter.toString(intClosedOpen);
  }
}
