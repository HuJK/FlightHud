package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class PitchIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;
  private final PitchIndicatorData pitchData = new PitchIndicatorData();

  public PitchIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    pitchData.update(dim);

    TextRenderer fontRenderer = client.textRenderer;
    int horizonOffset = i(computer.pitch * dim.degreesPerPixel);
    int yHorizon = dim.yMid + horizonOffset;

    for (int i = 20; i <= 90; i = i + 20) {
      int offset = i(dim.degreesPerPixel * i);
      drawDegreeBar(m, fontRenderer, -i, yHorizon + offset);
      drawDegreeBar(m, fontRenderer, i, yHorizon - offset);
    }

    pitchData.l1 -= pitchData.margin;
    pitchData.r2 += pitchData.margin;
    drawDegreeBar(m, fontRenderer, 0, yHorizon);

  }

  private void drawDegreeBar(MatrixStack matrixStack, TextRenderer fontRenderer, int degree,
      int y) {

    if (y < dim.tFrame || y > dim.bFrame) {
      return;
    }

    int dashes = degree < 0 ? 4 : 1;

    drawHorizontalLineDashed(matrixStack, pitchData.l1, pitchData.l2, y, COLOR, dashes);
    drawHorizontalLineDashed(matrixStack, pitchData.r1, pitchData.r2, y, COLOR, dashes);

    if (degree == 0) {
      int width = i((pitchData.l2 - pitchData.l1) * 0.25d);
      int l1 = pitchData.l2 - width;
      int r2 = pitchData.r1 + width;
      drawHorizontalLineDashed(matrixStack, l1, pitchData.l2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, pitchData.r1, r2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, l1, pitchData.l2, y + 6, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, pitchData.r1, r2, y + 6, COLOR, 3);
      return;
    }

    int sideTickHeight = degree >= 0 ? 5 : -5;
    drawVerticalLine(matrixStack, pitchData.l1, y, y + sideTickHeight, COLOR);
    drawVerticalLine(matrixStack, pitchData.r2, y, y + sideTickHeight, COLOR);

    int fontVerticalOffset = degree >= 0 ? 0 : 6;

    fontRenderer.draw(matrixStack, String.format("%d", Math.abs(degree)), pitchData.r2 + 6,
        (float) y - fontVerticalOffset, COLOR);

    fontRenderer.draw(matrixStack, String.format("%d", Math.abs(degree)), pitchData.l1 - 17,
        (float) y - fontVerticalOffset, COLOR);
  }

  private static class PitchIndicatorData {
    public int width;
    public int mid;
    public int margin;
    public int sideWidth;
    public int l1;
    public int l2;
    public int r1;
    public int r2;

    public void update(Dimensions dim) {
      width = i(dim.wScreen / 3);
      int left = width;

      mid = i((width / 2) + left);
      margin = i(width * 0.3d);
      l1 = left + margin;
      l2 = mid - 7;
      sideWidth = l2 - l1;
      r1 = mid + 7;
      r2 = r1 + sideWidth;
    }

    private int i(double d) {
      return (int) Math.round(d);
    }
  }

}