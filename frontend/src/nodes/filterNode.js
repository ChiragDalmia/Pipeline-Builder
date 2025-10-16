import { BaseNode } from "./baseNode";

export const FilterNode = ({ id, data }) => {
  const config = {
    title: "Filter",
    fields: [],
    handles: [
      { type: "target", id: "input" },
      { type: "source", id: "output" },
    ],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
