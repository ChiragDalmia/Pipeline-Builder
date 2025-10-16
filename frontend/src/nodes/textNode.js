"use client";
import { useState, useEffect } from "react";
import { BaseNode } from "./baseNode";

export const TextNode = ({ id, data }) => {
  const [currText, setCurrText] = useState(data?.text || "{{input}}");
  const [dimensions, setDimensions] = useState({ width: 200, height: 80 });
  const [variables, setVariables] = useState([]);

  useEffect(() => {
    const matches = [
      ...currText.matchAll(/\{\{\s*([a-zA-Z_$][a-zA-Z0-9_$]*)\s*\}\}/g),
    ];
    setVariables([...new Set(matches.map((match) => match[1].trim()))]);
  }, [currText]);

  useEffect(() => {
    const estimatedWidth = Math.max(
      200,
      Math.min(400, 200 + (currText.length * 7) / 3)
    );
    const estimatedLines = Math.ceil(
      currText.length / Math.floor(estimatedWidth / 7)
    );
    setDimensions({
      width: estimatedWidth,
      height: Math.max(80, 80 + (estimatedLines - 1) * 20),
    });
  }, [currText]);

  const config = {
    title: "Text",
    fields: [
      {
        label: "Text",
        render: () => (
          <textarea
            value={currText}
            onChange={(e) => setCurrText(e.target.value)}
            style={{
              width: "90%",
              minHeight: "60px",
              padding: "6px 8px",
              fontSize: "13px",
              border: "1px solid #d0d0d0",
              borderRadius: "6px",
              backgroundColor: "#fff",
              color: "#1e1e1e",
              outline: "none",
              transition: "border-color .2s ease, box-shadow .2s ease",
              fontFamily: "inherit",
              resize: "vertical",
            }}
            onFocus={(e) => {
              e.target.style.borderColor = "#6b9bd1";
              e.target.style.boxShadow = "0 0 0 2px rgba(107,155,209,.1)";
            }}
            onBlur={(e) => {
              e.target.style.borderColor = "#d0d0d0";
              e.target.style.boxShadow = "none";
            }}
          />
        ),
      },
    ],
    handles: [
      ...variables.map((varName) => ({
        type: "target",
        id: varName,
        label: varName,
      })),
      { type: "source", id: "output" },
    ],
    width: dimensions.width,
    height: dimensions.height,
  };

  return <BaseNode id={id} data={data} config={config} />;
};
